package com.wuyiccc.cookbook.network.hellonetty.util.concurent;

/**
 * @author wuyiccc
 * @date 2024/11/21 19:58
 */
public interface RejectedExecutionHandler {

    void rejected(Runnable task, SingleThreadEventExecutor executor);
}
