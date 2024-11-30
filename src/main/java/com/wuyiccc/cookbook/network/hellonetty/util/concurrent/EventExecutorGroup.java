package com.wuyiccc.cookbook.network.hellonetty.util.concurrent;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * @author wuyiccc
 * @date 2024/11/21 20:12
 *
 *  循环组接口
 */
public interface EventExecutorGroup extends Executor {

    EventExecutor next();

    void shutdownGracefully();


    boolean isTerminated();

    void awaitTermination(Integer integer, TimeUnit timeUnit) throws InterruptedException;
}
