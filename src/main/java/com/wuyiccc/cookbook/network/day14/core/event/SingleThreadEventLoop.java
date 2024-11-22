package com.wuyiccc.cookbook.network.day14.core.event;

import com.wuyiccc.cookbook.network.day14.core.executor.EventExecutorGroup;
import com.wuyiccc.cookbook.network.day14.core.executor.SingleThreadEventExecutor;
import com.wuyiccc.cookbook.network.day14.core.reject.RejectedExecutionHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.Executor;

/**
 * @author wuyiccc
 * @date 2024/11/22 21:51
 */
@Slf4j
public abstract class SingleThreadEventLoop extends SingleThreadEventExecutor implements EventLoop {

    protected static final int DEFAULT_MAX_PENDING_TASKS = Integer.MAX_VALUE;

    protected SingleThreadEventLoop(EventExecutorGroup parent
            , Executor executor
            , boolean addTaskWakesUp
            , Queue<Runnable> taskQueue
            , Queue<Runnable> tailTaskQueue
            , RejectedExecutionHandler rejectedExecutionHandler) {

        super(parent, executor, addTaskWakesUp, taskQueue, rejectedExecutionHandler);
    }

    @Override
    public EventLoopGroup parent() {

        return null;
    }

    @Override
    public EventLoop next() {

        return this;
    }

    // 注册服务端
    public void register(ServerSocketChannel channel, NioEventLoop nioEventLoop) {

        if (inEventLoop(Thread.currentThread())) {
            register0(channel, nioEventLoop);
        } else {
            nioEventLoop.execute(new Runnable() {
                @Override
                public void run() {

                    register0(channel, nioEventLoop);
                    log.info(">>> 服务器的channel已注册到多路复用器上了! : {}", Thread.currentThread().getName());
                }
            });
        }
    }


    // 服务器注册客户端read
    public void registerRead(SocketChannel channel, NioEventLoop nioEventLoop) {
        if (nioEventLoop.inEventLoop(Thread.currentThread())) {
            register00(channel, nioEventLoop);
        } else {
            nioEventLoop.execute(new Runnable() {
                @Override
                public void run() {

                    register00(channel, nioEventLoop);
                    log.info(">>> 客户端的channel已注册到多路复用器上看!: {}", Thread.currentThread().getName());
                }
            });
        }
    }

    // 客户端注册
    public void register(SocketChannel channel, NioEventLoop nioEventLoop) {

        if (nioEventLoop.inEventLoop(Thread.currentThread())) {
            register0(channel, nioEventLoop);
        } else {
            nioEventLoop.execute(new Runnable() {
                @Override
                public void run() {

                    register0(channel, nioEventLoop);
                    log.info(">>> 客户端的channel已注册到workGroup多路复用器上了!: {}", Thread.currentThread().getName());
                }
            });
        }
    }


    private void register0(SocketChannel channel, NioEventLoop nioEventLoop) {

        try {
            channel.configureBlocking(false);
            channel.register(nioEventLoop.unwrappedSelector(), SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            log.error(e.getMessage());
        }

    }

    private void register00(SocketChannel channel, NioEventLoop nioEventLoop) {

        try {

            channel.configureBlocking(false);
            channel.register(nioEventLoop.unwrappedSelector(), SelectionKey.OP_READ);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void register0(ServerSocketChannel channel, NioEventLoop nioEventLoop) {

        try {
            channel.configureBlocking(false);
            channel.register(nioEventLoop.unwrappedSelector(), SelectionKey.OP_ACCEPT);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
