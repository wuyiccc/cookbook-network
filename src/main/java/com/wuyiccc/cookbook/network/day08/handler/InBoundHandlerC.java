package com.wuyiccc.cookbook.network.day08.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author wuyiccc
 * @date 2024/7/2 22:53
 */
public class InBoundHandlerC extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        System.out.println("InBoundHandlerC: " + msg);


        // 从当前最尾部的handler向前传播
        //ctx.channel().writeAndFlush(msg);
        ctx.writeAndFlush(msg);
    }
}
