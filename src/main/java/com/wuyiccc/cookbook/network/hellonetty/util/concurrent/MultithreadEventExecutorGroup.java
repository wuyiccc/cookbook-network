package com.wuyiccc.cookbook.network.hellonetty.util.concurrent;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wuyiccc
 * @date 2024/11/21 21:41
 */
public abstract class MultithreadEventExecutorGroup extends AbstractEventExecutorGroup{

    // newChild创建的数组
    private final EventExecutor[] children;

    // 基于children构造的不可变集合
    private final Set<EventExecutor> readonlyChildren;

    private final AtomicInteger terminatedChildren = new AtomicInteger();

    private final EventExecutorChooserFactory.EventExecutorChooser chooser;

    protected MultithreadEventExecutorGroup(int nThreads, ThreadFactory threadFactory, Object... args) {

        this(nThreads, threadFactory == null ? null : new io.netty.util.concurrent.ThreadPerTaskExecutor(threadFactory), args);
    }

    protected MultithreadEventExecutorGroup(int nThreads, Executor executor, Object... args) {

        this(nThreads, executor, DefaultEventExecutorChooserFactory.INSTANCE, args);
    }

    protected MultithreadEventExecutorGroup(int nThreads, Executor executor, EventExecutorChooserFactory chooserFactory, Object... args) {

        if (nThreads <= 0) {
            throw new IllegalArgumentException(String.format("nThreads: %d (expected: > 0)", nThreads));
        }

        if (executor == null) {
            executor = new ThreadPerTaskExecutor(newDefaultThreadFactory());
        }

        children = new EventExecutor[nThreads];

        for (int i = 0; i < nThreads; i++) {
            boolean success = false;

            try {

                children[i] = newChild(executor, args);
                success = true;
            } catch (Exception e) {

                throw new IllegalStateException("failed to create a child event loop", e);
            } finally {

                if (!success) {
                    // 如果本次线程创建失败, 那么关闭之前创建的所有线程
                    for (int j = 0; j < i; j++) {
                        children[j].shutdownGracefully();
                    }

                    for (int j = 0; j < i; j++) {
                        EventExecutor e = children[j];

                        try {

                            // 判断正在关闭的执行器的状态, 如果还没有终止, 就等待一些时间再终止
                            while (!e.isTerminated()) {
                                e.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
                            }
                        } catch (InterruptedException interrupted) {
                            // 给当前线程设置一个中断标志
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }
            }
        }


        // 执行器选择器
        chooser = chooserFactory.newChooser(children);

        Set<EventExecutor> childrenSet = new LinkedHashSet<>(children.length);
        Collections.addAll(childrenSet, children);
        readonlyChildren = Collections.unmodifiableSet(childrenSet);
    }

    protected ThreadFactory newDefaultThreadFactory() {
        return new DefaultThreadFactory(getClass());
    }

    @Override
    public EventExecutor next() {

        return chooser.next();
    }

    @Override
    public void shutdownGracefully() {

        for (EventExecutor l : children) {
            l.shutdownGracefully();
        }
    }

    public final int executorCount() {

        return children.length;
    }

    protected abstract EventExecutor newChild(Executor executor, Object... args) throws Exception;
}

