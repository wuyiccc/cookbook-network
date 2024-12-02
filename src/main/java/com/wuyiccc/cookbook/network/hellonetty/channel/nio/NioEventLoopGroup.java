package com.wuyiccc.cookbook.network.hellonetty.channel.nio;

import com.wuyiccc.cookbook.network.hellonetty.channel.EventLoop;
import com.wuyiccc.cookbook.network.hellonetty.channel.EventLoopTaskQueueFactory;
import com.wuyiccc.cookbook.network.hellonetty.util.concurrent.EventExecutorChooserFactory;
import com.wuyiccc.cookbook.network.hellonetty.channel.MultithreadEventLoopGroup;
import com.wuyiccc.cookbook.network.hellonetty.util.concurrent.RejectedExecutionHandler;
import com.wuyiccc.cookbook.network.hellonetty.util.concurrent.RejectedExecutionHandlers;
import com.wuyiccc.cookbook.network.hellonetty.channel.DefaultSelectStrategyFactory;
import com.wuyiccc.cookbook.network.hellonetty.channel.SelectStrategyFactory;

import java.nio.channels.spi.SelectorProvider;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

/**
 * @author wuyiccc
 * @date 2024/11/22 22:12
 */
public class NioEventLoopGroup extends MultithreadEventLoopGroup {

    public NioEventLoopGroup() {
        this(0);
    }

    public NioEventLoopGroup(int nThreads) {

        this(nThreads, (Executor) null);
    }

    public NioEventLoopGroup(int nThreads, ThreadFactory threadFactory) {

        this(nThreads, threadFactory, SelectorProvider.provider());
    }

    public NioEventLoopGroup(int nThreads, Executor executor) {

        this(nThreads, executor, SelectorProvider.provider());
    }

    public NioEventLoopGroup(int nThreads, ThreadFactory threadFactory, final SelectorProvider selectorProvider) {

        this(nThreads, threadFactory, selectorProvider, DefaultSelectStrategyFactory.INSTANCE);
    }

    public NioEventLoopGroup(int nThreads, ThreadFactory threadFactory, final SelectorProvider selectorProvider, final SelectStrategyFactory selectStrategyFactory) {

        super(nThreads, threadFactory, selectorProvider, selectStrategyFactory, RejectedExecutionHandlers.reject());
    }

    public NioEventLoopGroup(int nThreads, Executor executor, final SelectorProvider selectorProvider) {

        this(nThreads, executor, selectorProvider, DefaultSelectStrategyFactory.INSTANCE);
    }

    public NioEventLoopGroup(int nThreads, Executor executor, final SelectorProvider selectorProvider,
                             final SelectStrategyFactory selectStrategyFactory) {

        super(nThreads, executor, selectorProvider, selectStrategyFactory, RejectedExecutionHandlers.reject());
    }

    public NioEventLoopGroup(int nThreads, Executor executor, EventExecutorChooserFactory chooserFactory,
                             final SelectorProvider selectorProvider,
                             final SelectStrategyFactory selectStrategyFactory) {

        super(nThreads, executor, chooserFactory, selectorProvider, selectStrategyFactory,
                RejectedExecutionHandlers.reject());
    }

    public NioEventLoopGroup(int nThreads, Executor executor, EventExecutorChooserFactory chooserFactory,
                             final SelectorProvider selectorProvider,
                             final SelectStrategyFactory selectStrategyFactory,
                             final RejectedExecutionHandler rejectedExecutionHandler) {

        super(nThreads, executor, chooserFactory, selectorProvider, selectStrategyFactory, rejectedExecutionHandler);
    }

    public NioEventLoopGroup(int nThreads, Executor executor, EventExecutorChooserFactory chooserFactory,
                             final SelectorProvider selectorProvider,
                             final SelectStrategyFactory selectStrategyFactory,
                             final RejectedExecutionHandler rejectedExecutionHandler,
                             final EventLoopTaskQueueFactory taskQueueFactory) {

        super(nThreads, executor, chooserFactory, selectorProvider, selectStrategyFactory,
                rejectedExecutionHandler, taskQueueFactory);
    }

    // args: selectorProvider, selectStrategyFactory, rejectExecutionHandler, taskQueueFactory(可选)
    @Override
    protected EventLoop newChild(Executor executor, Object... args) throws Exception {

        EventLoopTaskQueueFactory queueFactory = args.length == 4 ? (EventLoopTaskQueueFactory) args[3] : null;

        return new NioEventLoop(this, executor, (SelectorProvider) args[0],
                ((SelectStrategyFactory) args[1]).newSelectStrategy(), (RejectedExecutionHandler) args[2], queueFactory);
    }
}
