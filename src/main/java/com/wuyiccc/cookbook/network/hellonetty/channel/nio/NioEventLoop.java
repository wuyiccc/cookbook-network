package com.wuyiccc.cookbook.network.hellonetty.channel.nio;

import com.wuyiccc.cookbook.network.hellonetty.channel.EventLoopGroup;
import com.wuyiccc.cookbook.network.hellonetty.channel.EventLoopTaskQueueFactory;
import com.wuyiccc.cookbook.network.hellonetty.channel.SingleThreadEventLoop;
import com.wuyiccc.cookbook.network.hellonetty.util.concurrent.RejectedExecutionHandler;
import com.wuyiccc.cookbook.network.hellonetty.channel.SelectStrategy;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
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
    }

    private static Queue<Runnable> newTaskQueue(EventLoopTaskQueueFactory queueFactory) {

        if (queueFactory == null) {
            return new LinkedBlockingQueue<>(DEFAULT_MAX_PENDING_TASKS);
        }

        return queueFactory.newTaskQueue(DEFAULT_MAX_PENDING_TASKS);
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
                // 如果没有事件就阻塞在这里: io事件/task任务
                select();
                // 如果有事件就处理事件
                processSelectedKeys();
            } catch (Exception e) {
                log.error("run异常", e);
            } finally {
                // 执行单线程执行器中所有的任务
                runAllTasks();
            }
        }
    }


    private void select() throws IOException {
        Selector selector = this.selector;
        for (; ; ) {
            log.info("我还不是netty, 我要阻塞在这里1s, 当然, 即便我是netty, 我也会阻塞在这里");
            int selectedKeys = selector.select(1000);
            // 如果有事件或者单线程执行器中有任务待之下, 就退出循环
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
            final Object a = k.attachment();
            i.remove();

            // 处理就绪事件
            if (a instanceof AbstractNioChannel) {
                processSelectedKey(k, (AbstractNioChannel) a);
            }

            if (!i.hasNext()) {
                break;
            }
        }
    }


    // AbstractNioChannel作为抽象类, 既可以调用服务端channel的方法, 也可以调用客户端channel的方法
    // 这样就巧妙的把客户端和服务端的channel与nioEventLoop解耦了
    private void processSelectedKey(SelectionKey k, AbstractNioChannel ch) throws IOException {

        // 如果当前是客户端的事件轮询处理器
        try {
            // TODO(wuyiccc): 这里netty源码调用的方法是readyOps, 作者后面的新版本代码也采用了readyOps, 目前这个版本不知道为什么用这个方法
            // 得到key感兴趣的事件
            int ops = k.interestOps();
            // 如果是连接事件
            if (ops == SelectionKey.OP_CONNECT) {
                // 移除连接事件, 否则会一直通知
                ops &= ~SelectionKey.OP_CONNECT;
                // 重新把感兴趣的事件注册一下
                k.interestOps(ops);
                // 然后再注册客户端channel感兴趣的读事件
                ch.doBeginRead();
            }

            // 如果是读事件, 不管是客户端还是服务端, 都可以直接调用read方法
            if (ops == SelectionKey.OP_READ) {
                ch.read();
            }
            if (ops == SelectionKey.OP_ACCEPT) {
                ch.read();
            }

        } catch (CancelledKeyException ignored) {
            throw new RuntimeException(ignored.getMessage());
        }


    }


}
