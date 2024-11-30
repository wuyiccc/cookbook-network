package com.wuyiccc.cookbook.network.hellonetty.util.concurent;

/**
 * @author wuyiccc
 * @date 2024/11/21 20:14
 */
public interface EventExecutor extends EventExecutorGroup {


    @Override
    EventExecutor next();

    EventExecutorGroup parent();


    boolean inEventLoop(Thread thread);

}
