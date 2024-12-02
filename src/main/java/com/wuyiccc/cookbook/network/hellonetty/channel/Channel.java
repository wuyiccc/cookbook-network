package com.wuyiccc.cookbook.network.hellonetty.channel;

import java.net.SocketAddress;

/**
 * @author wuyiccc
 * @date 2024/11/30 17:18
 */
public interface Channel {

    ChannelId id();

    // 一个channel只能对应一个EventLoop
    EventLoop eventLoop();

    Channel parent();

    ChannelConfig config();

    boolean isOpen();

    boolean isRegistered();

    boolean isActive();

    SocketAddress localAddress();

    SocketAddress remoteAddress();

    ChannelFuture closeFuture();

    ChannelFuture close();

    void bind(SocketAddress localAddress, ChannelPromise promise);

    void connect(SocketAddress remoteAddress, final SocketAddress localAddress, ChannelPromise promise);


    // 不管是客户端channel, 还是服务端channel, 都需要调用register, 将自身java.channel注册到selector上
    void register(EventLoop eventLoop, ChannelPromise promise);

    void beginRead();



}
