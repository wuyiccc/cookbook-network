package com.wuyiccc.cookbook.network.day13;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author wuyiccc
 * @date 2024/11/17 11:51
 */
public class SingleThreadEventExecutor implements Executor {

    private static final Logger log = LoggerFactory.getLogger(SingleThreadEventExecutor.class);


    protected static final int DEFAULT_MAX_PENDING_TASKS = Integer.MAX_VALUE;

    private final Queue<Runnable> taskQueue;


    private final RejectedExecutionHandler rejectedExecutionHandler;

    private volatile boolean start = false;

    private final SelectorProvider provider;

    private final Selector selector;

    private Thread thread;

    public SingleThreadEventExecutor() {

        this.provider = SelectorProvider.provider();
        this.taskQueue = newTaskQueue(DEFAULT_MAX_PENDING_TASKS);
        this.rejectedExecutionHandler = new ThreadPoolExecutor.AbortPolicy();
        this.selector = openSelector();
    }

    protected Queue<Runnable> newTaskQueue(int maxPendingTasks) {

        return new LinkedBlockingQueue<>(maxPendingTasks);
    }

    protected Selector openSelector() {

        try {
            return provider.openSelector();
        } catch (IOException e) {
            throw new RuntimeException("failed to open a new selector");
        }
    }

    @Override
    public void execute(Runnable task) {

        if (task == null) {
            throw new NullPointerException("task");
        }

        // 把任务提交到任务队列中
        addTask(task);

        // 启动单线程执行器中的线程
        startThread();
    }

    private void addTask(Runnable task) {

        if (task == null) {
            throw new NullPointerException("task");
        }

        if (!offerTask(task)) {
            // 如果添加失败, 执行拒绝策略
            reject(task);
        }
    }

    final boolean offerTask(Runnable task) {

        return taskQueue.offer(task);
    }

    protected final void reject(Runnable task) {
        //rejectedExecutionHandler.rejectedExecution(task, this);
    }

    private void startThread() {

        if (start) {
            return;
        }

        start = true;

        new Thread(() -> {

            // 存储新创建的线程
            thread = Thread.currentThread();
            // 执行run方法, 在run方法中就是对io的事件的处理
            SingleThreadEventExecutor.this.run();
        }).start();

        System.out.println("新线程创建了");
    }

    public void run() {

        while (true) {

            try {
                select();
                processSelectedKeys(selector.selectedKeys());
            } catch (Exception e) {
                log.error("run方法执行异常", e);
            } finally {
                runAllTasks();
            }
        }
    }

    protected boolean hasTasks() {

        return !taskQueue.isEmpty();
    }


    protected void runAllTasks() {

        runAllTasksFrom(taskQueue);
    }

    protected void runAllTasksFrom(Queue<Runnable> taskQueue) {

        Runnable task = pollTaskFrom(taskQueue);
        if (task == null) {
            return;
        }

        for (; ; ) {
            safeExecute(task);
            task = pollTaskFrom(taskQueue);
            if (task == null) {
                return;
            }
        }
    }


    private void safeExecute(Runnable task) {

        try {

            task.run();
        } catch (Throwable t) {
            System.err.println("A task raised an exception, Task: " + task + "\n" + t);
        }
    }

    protected static Runnable pollTaskFrom(Queue<Runnable> taskQueue) {

        return taskQueue.poll();
    }

    /**
     * 判断当前线程是否是执行任务的线程
     */
    public boolean inEventLoop(Thread thread) {

        return thread == this.thread;
    }


    public void register(SocketChannel socketChannel) {

        if (inEventLoop(Thread.currentThread())) {

            register0(socketChannel);
        } else {

            this.execute(() -> {
                register0(socketChannel);
                log.info("客户端的channel已经注册到新线程的多路复用器上了!");
            });
        }
    }

    private void register0(SocketChannel socketChannel) {

        try {

            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
        } catch (Exception e) {

            System.err.println(e.getLocalizedMessage());
        }
    }

    private void select() throws IOException {

        Selector selector = this.selector;

        for (; ; ) {

            System.out.println("新线程就阻塞在这里3秒吧...");
            // 因为单线程执行器不仅要执行select任务还要执行客户端channel注册任务, 所以不能一直卡在select这里
            int selectedKeys = selector.select(3000);

            if (selectedKeys != 0 || hasTasks()) {
                break;
            }
        }
    }

    private void processSelectedKeys(Set<SelectionKey> selectedKeys) throws IOException {

        if (selectedKeys.isEmpty()) {
            return;
        }

        Iterator<SelectionKey> it = selectedKeys.iterator();

        for (; ; ) {

            final SelectionKey k = it.next();
            it.remove();

            // 处理就绪事件
            processSelectedKey(k);

            if (!it.hasNext()) {
                break;
            }
        }
    }

    private void processSelectedKey(SelectionKey k) throws IOException {

        if (k.isReadable()) {

            SocketChannel channel = (SocketChannel) k.channel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

            int len = channel.read(byteBuffer);

            if (len == -1) {
                log.info("客户端通道要关闭");
                channel.close();
                return;
            }

            byte[] bytes = new byte[len];

            byteBuffer.flip();
            byteBuffer.get(bytes);

            log.info("新线程收到客户端发送的数据: {}", new String(bytes));
        }
    }


}
