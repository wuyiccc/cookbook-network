package com.wuyiccc.cookbook.network.day14.core.reject;

import com.wuyiccc.cookbook.network.day14.core.executor.SingleThreadEventExecutor;

/**
 * @author wuyiccc
 * @date 2024/11/21 19:58
 */
public interface RejectedExecutionHandler {

    void rejected(Runnable task, SingleThreadEventExecutor executor);
}
