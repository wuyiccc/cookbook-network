package com.wuyiccc.cookbook.network.hellonetty.channel;

import com.wuyiccc.cookbook.network.hellonetty.util.concurent.EventExecutor;

/**
 * @author wuyiccc
 * @date 2024/11/21 20:28
 */
public interface EventLoop extends EventExecutor, EventLoopGroup {

    @Override
    EventLoopGroup parent();
}
