package com.wuyiccc.cookbook.network.hellonetty.channel;

import com.wuyiccc.cookbook.network.hellonetty.util.concurrent.EventExecutorGroup;
import com.wuyiccc.cookbook.network.hellonetty.util.concurrent.RejectedExecutionHandler;
import com.wuyiccc.cookbook.network.hellonetty.util.concurrent.SingleThreadEventExecutor;
import com.wuyiccc.cookbook.network.hellonetty.util.internal.ObjectUtil;
import lombok.extern.slf4j.Slf4j;

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

    @Override
    protected boolean hasTasks() {
        return super.hasTasks();
    }

    @Override
    public ChannelFuture register(Channel channel) {

        return register(new DefaultChannelPromise(channel, this));
    }

    @Override
    public ChannelFuture register(final ChannelPromise promise) {

        ObjectUtil.checkNotNull(promise, "promise");
        promise.channel().unsafe().register(this, promise);
        return promise;
    }

}
