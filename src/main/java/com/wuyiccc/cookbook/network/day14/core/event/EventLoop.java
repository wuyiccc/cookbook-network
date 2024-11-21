package com.wuyiccc.cookbook.network.day14.core.event;

import com.wuyiccc.cookbook.network.day14.core.executor.EventExecutor;
import com.wuyiccc.cookbook.network.day14.core.executor.EventExecutorGroup;

/**
 * @author wuyiccc
 * @date 2024/11/21 20:28
 */
public interface EventLoop extends EventExecutor, EventLoopGroup {

    @Override
    EventLoopGroup parent();
}
