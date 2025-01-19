package com.wuyiccc.cookbook.network.mydemo.mydemo.simple.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author wuyiccc
 * @date 2025/1/19 13:32
 */
public class ServerPrintHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        System.out.println("有客户端连接过来了!");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        ByteBuf buf = (ByteBuf) msg;
        int msgLength = buf.readInt();
        System.out.println("消息长度: " + msgLength);

        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        System.out.println(new String(bytes));
        //buf.release();
        ctx.fireChannelRead(msg);
    }


}
