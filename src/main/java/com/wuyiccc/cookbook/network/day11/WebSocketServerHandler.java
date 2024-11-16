package com.wuyiccc.cookbook.network.day11;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @author wuyiccc
 * @date 2024/11/16 11:29
 */
public class WebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {


    private static ChannelGroup webSocketClients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {

        // websocket查看网页客户端发送过来的数据
        String request = msg.text();

        TextWebSocketFrame response = new TextWebSocketFrame("hello, i'm websocket server");

        ctx.writeAndFlush(response);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

        webSocketClients.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {

        super.handlerRemoved(ctx);
        webSocketClients.remove(ctx.channel());
    }
}
