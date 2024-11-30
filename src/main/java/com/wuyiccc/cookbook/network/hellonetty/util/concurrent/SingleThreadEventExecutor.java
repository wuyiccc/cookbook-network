package com.wuyiccc.cookbook.network.hellonetty.util.concurrent;

import com.wuyiccc.cookbook.network.hellonetty.util.internal.ObjectUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * @author wuyiccc
 * @date 2024/11/22 20:39
 */
@Slf4j
public abstract class SingleThreadEventExecutor implements EventExecutor {

    // 执行器的初始状态, 未启动
    private static final int ST_NOT_STARTED = 1;

    // 执行器启动后的状态
    private static final int ST_STARTED = 2;


    private volatile int state = ST_NOT_STARTED;


    // 执行器的状态更新器, 也是一个原子类, 通过cas来改变执行器的状态值
    private static final AtomicIntegerFieldUpdater<SingleThreadEventExecutor> STATE_UPLOAD =
            AtomicIntegerFieldUpdater.newUpdater(SingleThreadEventExecutor.class, "state");

    // 任务队列的容量, 默认是Integer的最大值
    protected static final int DEFAULT_MAX_PENDING_EXECUTOR_TASKS = Integer.MAX_VALUE;

    private final Queue<Runnable> taskQueue;

    private volatile Thread thread;

    // 创建线程的执行器
    private Executor executor;

    private EventExecutorGroup parent;


    private boolean addTaskWakesUp;


    private volatile boolean interrupted;


    private final RejectedExecutionHandler rejectedExecutionHandler;


    protected SingleThreadEventExecutor(EventExecutorGroup parent, Executor executor
            , boolean addTaskWakesUp
            , Queue<Runnable> taskQueue
            , RejectedExecutionHandler rejectedHandler) {

        this.parent = parent;
        this.addTaskWakesUp = addTaskWakesUp;
        this.executor = executor;
        this.taskQueue = ObjectUtil.checkNotNull(taskQueue, "taskQueue");
        this.rejectedExecutionHandler = ObjectUtil.checkNotNull(rejectedHandler, "rejectedHandler");
    }

    protected Queue<Runnable> newTaskQueue(int maxPendingTasks) {

        return new LinkedBlockingQueue<>(maxPendingTasks);
    }

    // 该方法在NioEventLoop中实现, 是真正执行轮询的方法
    protected abstract void run();

    @Override
    public void execute(Runnable task) {

        if (task == null) {
            throw new NullPointerException("task");
        }

        // 将任务添加到任务队列中
        addTask(task);
        // 启动单线程执行器中的线程
        startThread();
    }

    private void startThread() {

        // 暂不考虑特别全面的线程池状态, 只关心线程是否启动
        // 如果执行器的状态是未启动, 就cas将其状态值变为已启动
        if (state == ST_NOT_STARTED) {

            if (STATE_UPLOAD.compareAndSet(this, ST_NOT_STARTED, ST_STARTED)) {
                boolean success = false;
                try {
                    doStartThread();
                    success = true;
                } finally {

                    // 如果未启动成功, 直接把状态复原
                    if (!success) {
                        STATE_UPLOAD.compareAndSet(this, ST_STARTED, ST_NOT_STARTED);
                    }
                }
            }
        }
    }

    private void doStartThread() {

        // 这里的executor的ThreadPerTaskExecutor, runnable ->
        executor.execute(new Runnable() {
            @Override
            public void run() {

                thread = Thread.currentThread();
                if (interrupted) {
                    thread.interrupt();
                }

                SingleThreadEventExecutor.this.run();
                log.info(">>> 单线程执行器的线程错误结束了!");
            }
        });
    }

    @Override
    public boolean inEventLoop(Thread thread) {

        return thread == this.thread;
    }

    protected boolean hasTasks() {

        return !taskQueue.isEmpty();
    }

    private void addTask(Runnable task) {

        if (task == null) {
            throw new NullPointerException("task");
        }

        // 如果添加失败, 执行拒绝策略
        if (!offerTask(task)) {
            reject(task);
        }
    }

    final boolean offerTask(Runnable task) {

        return taskQueue.offer(task);
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
        } catch (Throwable e) {
            log.error("A task raised an exception, Task: {}", task, e);
        }
    }

    protected static Runnable pollTaskFrom(Queue<Runnable> taskQueue) {
        return taskQueue.poll();
    }

    protected static void reject() {

        throw new RejectedExecutionException("event executor terminated");
    }

    protected final void reject(Runnable task) {
        rejectedExecutionHandler.rejected(task, this);
    }

    /**
     * 中断单线程执行器中的线程
     */
    protected void interruptThread() {

        Thread currentThread = thread;
        if (currentThread == null) {
            interrupted = true;
        } else {
            currentThread.interrupt();
        }
    }

    @Override
    public void shutdownGracefully() {


    }


    @Override
    public boolean isTerminated() {

        return false;
    }

    @Override
    public void awaitTermination(Integer integer, TimeUnit timeUnit) throws InterruptedException {

    }

    public Queue<Runnable> getTaskQueue() {
        return taskQueue;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public Executor getExecutor() {
        return executor;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public RejectedExecutionHandler getRejectedExecutionHandler() {
        return rejectedExecutionHandler;
    }
}
