package com.wuyiccc.cookbook.network.day10;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

import java.nio.charset.StandardCharsets;

/**
 * @author wuyiccc
 * @date 2024/11/16 10:50
 */
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {


        String method = msg.getMethod().name();
        String uri = msg.getUri();

        System.out.println("请求信息: method: " + method + ", uri: " + uri);

        // 创建响应
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        String html = "<h1>nihao</h1>";

        ByteBuf byteBuf = Unpooled.copiedBuffer(html, StandardCharsets.UTF_8);
        response.headers().set("content-type", "text/html;charset=UTF-8");

        response.content().writeBytes(byteBuf);

        byteBuf.release();

        ctx.channel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
