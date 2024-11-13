package com.wuyiccc.cookbook.network.day08.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

/**
 * @author wuyiccc
 * @date 2024/7/2 22:56
 */
public class OutBoundHandlerB extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

        System.out.println("OutBoundHandlerB: " + msg);
        ctx.write(msg);
    }
}
