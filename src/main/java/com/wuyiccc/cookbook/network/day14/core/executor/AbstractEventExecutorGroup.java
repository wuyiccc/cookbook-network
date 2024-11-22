package com.wuyiccc.cookbook.network.day14.core.executor;

import java.util.concurrent.TimeUnit;

/**
 * @author wuyiccc
 * @date 2024/11/21 21:27
 */
public abstract class AbstractEventExecutorGroup implements EventExecutorGroup {

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

    @Override
    public void execute(Runnable command) {

        next().execute(command);
    }
}
