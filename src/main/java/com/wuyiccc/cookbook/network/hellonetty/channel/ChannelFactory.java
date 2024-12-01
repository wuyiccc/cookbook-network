package com.wuyiccc.cookbook.network.hellonetty.channel;

/**
 * @author wuyiccc
 * @date 2024/11/30 22:06
 */
public interface ChannelFactory<T extends Channel> {

    T newChannel();
}
