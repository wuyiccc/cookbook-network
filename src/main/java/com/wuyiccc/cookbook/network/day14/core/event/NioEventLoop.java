package com.wuyiccc.cookbook.network.day14.core.event;

import com.wuyiccc.cookbook.network.day14.core.EventLoopTaskQueueFactory;
import com.wuyiccc.cookbook.network.day14.core.reject.RejectedExecutionHandler;
import com.wuyiccc.cookbook.network.day14.core.strategy.SelectStrategy;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author wuyiccc
 * @date 2024/11/22 21:56
 */
@Slf4j
public class NioEventLoop extends SingleThreadEventLoop {

    private EventLoopGroup workerGroup;

    private static int index = 0;

    private int id = 0;

    private ServerSocketChannel serverSocketChannel;

    private SocketChannel socketChannel;

    private final Selector selector;

    private final SelectorProvider provider;

    private SelectStrategy selectStrategy;


    NioEventLoop(NioEventLoopGroup parent, Executor executor, SelectorProvider selectorProvider
            , SelectStrategy strategy
            , RejectedExecutionHandler rejectedExecutionHandler
            , EventLoopTaskQueueFactory queueFactory) {

        super(parent, executor, false, newTaskQueue(queueFactory), newTaskQueue(queueFactory),
                rejectedExecutionHandler);

        if (selectorProvider == null) {
            throw new NullPointerException("selectorProvider");
        }

        if (strategy == null) {
            throw new NullPointerException("selectStrategy");
        }

        provider = selectorProvider;
        selector = openSelector();
        selectStrategy = strategy;
        log.info("我是" + ++index + "nioEventLoop");
        id = index;
        log.info("work: " + id);
    }

    private static Queue<Runnable> newTaskQueue(EventLoopTaskQueueFactory queueFactory) {

        if (queueFactory == null) {
            return new LinkedBlockingQueue<>(DEFAULT_MAX_PENDING_TASKS);
        }

        return queueFactory.newTaskQueue(DEFAULT_MAX_PENDING_TASKS);
    }

    public void setServerSocketChannel(ServerSocketChannel serverSocketChannel) {

        this.serverSocketChannel = serverSocketChannel;
    }

    public void setSocketChannel(SocketChannel socketChannel) {

        this.socketChannel = socketChannel;
    }

    public void setWorkerGroup(EventLoopGroup workerGroup) {
        this.workerGroup = workerGroup;
    }

    private Selector openSelector() {

        final Selector unwrappedSelector;

        try {
            unwrappedSelector = provider.openSelector();
            return unwrappedSelector;
        } catch (IOException e) {
            throw new RuntimeException("failed to open a new selector");
        }
    }


    public Selector unwrappedSelector() {
        return selector;
    }

    @Override
    protected void run() {

        for (; ; ) {
            try {
                select();
                processSelectedKeys();
            } catch (Exception e) {
                log.error("run异常", e);
            } finally {
                runAllTasks();
            }
        }
    }


    private void select() throws IOException {
        Selector selector = this.selector;
        for (; ; ) {
            log.info("我还不是netty, 我要阻塞在这里3s, 当然, 即便我是netty, 我也会阻塞在这里");
            int selectedKeys = selector.select(3000);
            if (selectedKeys != 0 || hasTasks()) {
                break;
            }
        }
    }

    private void processSelectedKeys() throws IOException {

        processSelectedKeysPlain(selector.selectedKeys());
    }

    private void processSelectedKeysPlain(Set<SelectionKey> selectedKeys) throws IOException {

        if (selectedKeys.isEmpty()) {
            return;
        }

        Iterator<SelectionKey> i = selectedKeys.iterator();
        for (; ; ) {
            final SelectionKey k = i.next();
            i.remove();
            processSelectedKey(k);
            if (!i.hasNext()) {
                break;
            }
        }
    }


    private void processSelectedKey(SelectionKey k) throws IOException {

        // 如果当前是客户端的事件轮询处理器
        if (socketChannel != null) {

            // 兼容处理client端的SocketChannel
            if (k.isConnectable()) {
                if (socketChannel.finishConnect()) {
                    socketChannel.register(selector, SelectionKey.OP_READ);
                }
            }

            if (k.isReadable()) {
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                int len = socketChannel.read(byteBuffer);
                byte[] buffer = new byte[len];
                byteBuffer.flip();
                byteBuffer.get(buffer);
                log.info(">>> 客户端收到消息: {}", new String(buffer));
            }
            return;
        }

        // 如果当前是服务端的事件轮询处理器
        if (serverSocketChannel != null) {

            if (k.isAcceptable()) {
                SocketChannel socketChannel1 = serverSocketChannel.accept();
                socketChannel1.configureBlocking(false);
                NioEventLoop nioEventLoop = (NioEventLoop) workerGroup.next().next();
                nioEventLoop.setServerSocketChannel(serverSocketChannel);
                log.info("+++++++++++++++++++++++++++++++++++++++++++要注册到第" + nioEventLoop.id + "work上！");
                //work线程自己注册的channel到执行器
                nioEventLoop.registerRead(socketChannel1, nioEventLoop);
                log.info("客户端连接成功:{}", socketChannel1.toString());
                socketChannel1.write(ByteBuffer.wrap("我还不是netty，但我知道你上线了".getBytes()));
                log.info("服务器发送消息成功！");
            }

            if (k.isReadable()) {
                SocketChannel channel = (SocketChannel) k.channel();
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                int len = channel.read(byteBuffer);
                if (len == -1) {
                    log.info("客户端通道要关闭！");
                    channel.close();
                    return;
                }
                byte[] bytes = new byte[len];
                byteBuffer.flip();
                byteBuffer.get(bytes);
                log.info("收到客户端发送的数据:{}", new String(bytes));
            }
        }

    }


}
