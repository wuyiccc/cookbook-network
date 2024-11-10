package com.wuyiccc.cookbook.network.day06;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;

/**
 * @author wuyiccc
 * @date 2024/11/10 15:19
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        // 1. 获得客户端请求的内容
        ByteBuf buffer = (ByteBuf) msg;

        byte[] requestBytes = new byte[buffer.readableBytes()];
        buffer.readBytes(requestBytes);

        String request = new String(requestBytes, StandardCharsets.UTF_8);

        System.out.println("收到请求: " + request);

        // 向客户端返回信息
        String response = "收到请求后返回响应";
        ByteBuf responseBuffer = Unpooled.copiedBuffer(response.getBytes(StandardCharsets.UTF_8));
        ctx.write(responseBuffer);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

        // 真正的发送
        ctx.flush();
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Server is Active");
    }
}
