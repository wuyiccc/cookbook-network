package com.wuyiccc.cookbook.network.mydemo.mydemo.demo01;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class MyClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();

        byteBuf.writeBytes("你好啊, 我是客户端".getBytes());
        ChannelFuture channelFuture = ctx.channel().writeAndFlush(byteBuf);
        channelFuture.sync();
    }
}
