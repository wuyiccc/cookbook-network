package com.wuyiccc.cookbook.network.hellonetty.channel;

import com.wuyiccc.cookbook.network.hellonetty.util.concurrent.EventExecutorGroup;

/**
 * @author wuyiccc
 * @date 2024/11/21 20:28
 */
public interface EventLoopGroup extends EventExecutorGroup {

    @Override
    EventLoop next();
}
