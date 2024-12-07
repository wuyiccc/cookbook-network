package com.wuyiccc.cookbook.network.hellonetty.channel;

import com.wuyiccc.cookbook.network.hellonetty.util.AttributeMap;

import java.net.SocketAddress;

/**
 * @author wuyiccc
 * @date 2024/11/30 17:18
 */
public interface Channel extends AttributeMap, ChannelOutboundInvoker {

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


    Unsafe unsafe();

    @Override
    Channel read();

    @Override
    Channel flush();


    interface Unsafe {

        SocketAddress localAddress();

        SocketAddress remoteAddress();


        void register(EventLoop eventLoop, ChannelPromise promise);

        void bind(SocketAddress localAddress, ChannelPromise promise);

        void connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise);

        void disconnect(ChannelPromise promise);

        void close(ChannelPromise promise);

        void closeForcibly();

        void deregister(ChannelPromise promise);

        void beginRead();

        void write(Object msg, ChannelPromise promise);

        void flush();

    }


}
