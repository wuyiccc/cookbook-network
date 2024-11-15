package com.wuyiccc.cookbook.network.day08.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author wuyiccc
 * @date 2024/7/2 22:53
 */
public class InBoundHandlerA extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        System.out.println("InBoundHandlerA: " + msg);
        super.channelRead(ctx, msg);
    }
}