package com.wuyiccc.cookbook.network.hellonetty.channel;

import com.wuyiccc.cookbook.network.hellonetty.util.concurrent.EventExecutorChooserFactory;
import com.wuyiccc.cookbook.network.hellonetty.util.concurrent.EventExecutor;
import com.wuyiccc.cookbook.network.hellonetty.util.concurrent.MultiThreadEventExecutorGroup;
import com.wuyiccc.cookbook.network.hellonetty.NettyRuntime;
import com.wuyiccc.cookbook.network.hellonetty.util.concurrent.DefaultThreadFactory;
import com.wuyiccc.cookbook.network.hellonetty.util.internal.SystemPropertyUtil;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

/**
 * @author wuyiccc
 * @date 2024/11/21 23:00
 */
public abstract class MultiThreadEventLoopGroup extends MultiThreadEventExecutorGroup implements EventLoopGroup {


    private static final int DEFAULT_EVENT_LOOP_THREADS;

    static {

        DEFAULT_EVENT_LOOP_THREADS = Math.max(1, SystemPropertyUtil.getInt("io.netty.eventLoopThreads", NettyRuntime.availableProcessors() * 2));
    }

    protected MultiThreadEventLoopGroup(int nThreads, Executor executor, Object... args) {

        super(nThreads == 0 ? DEFAULT_EVENT_LOOP_THREADS : nThreads, executor, args);
    }


    protected MultiThreadEventLoopGroup(int nThreads, ThreadFactory threadFactory, Object... args) {

        super(nThreads == 0 ? DEFAULT_EVENT_LOOP_THREADS : nThreads, threadFactory, args);
    }

    protected MultiThreadEventLoopGroup(int nThreads, Executor executor, EventExecutorChooserFactory chooserFactory, Object... args) {

        super(nThreads == 0 ? DEFAULT_EVENT_LOOP_THREADS : nThreads, executor, chooserFactory, args);
    }

    @Override
    protected ThreadFactory newDefaultThreadFactory() {

        return new DefaultThreadFactory(getClass(), Thread.MAX_PRIORITY);
    }

    @Override
    public EventLoop next() {

        return (EventLoop) super.next();
    }

    @Override
    protected abstract EventExecutor newChild(Executor executor, Object... args) throws Exception;

}
