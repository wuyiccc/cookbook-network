package com.wuyiccc.cookbook.network.hellonetty.channel;

import java.net.SocketAddress;

/**
 * @author wuyiccc
 * @date 2024/12/3 11:27
 */
public interface ChannelOutboundInvoker {

    ChannelFuture bind(SocketAddress localAddress);

    ChannelFuture connect(SocketAddress remoteAddress);

    ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress);

    ChannelFuture disconnect();

    ChannelFuture close();

    ChannelFuture deregister();

    ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise);

    ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise);

    ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise);

    ChannelFuture disconnect(ChannelPromise promise);

    ChannelFuture close(ChannelPromise promise);

    ChannelFuture deregister(ChannelPromise promise);

    ChannelOutboundInvoker read();

    ChannelFuture write(Object msg);

    ChannelFuture write(Object msg, ChannelPromise promise);

    ChannelOutboundInvoker flush();

    ChannelFuture writeAndFlush(Object msg, ChannelPromise promise);

    ChannelFuture writeAndFlush(Object msg);

    ChannelPromise newPromise();

    ChannelFuture newSucceededFuture();

    ChannelFuture newFailedFuture(Throwable cause);

}
