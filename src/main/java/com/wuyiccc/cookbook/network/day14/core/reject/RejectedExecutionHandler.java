package com.wuyiccc.cookbook.network.day14.core.reject;

/**
 * @author wuyiccc
 * @date 2024/11/21 19:58
 */
public interface RejectedExecutionHandler {

    void rejected(Runnable task, Object executor);
}
