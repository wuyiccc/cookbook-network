package com.wuyiccc.cookbook.network.day06;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;

import java.nio.charset.StandardCharsets;

/**
 * @author wuyiccc
 * @date 2024/11/12 20:36
 */
public class HandleLifeCycle implements ChannelInboundHandler {

    private ByteBuf requestBuffer;

    public HandleLifeCycle() {
        byte[] requestBytes = "发送请求".getBytes(StandardCharsets.UTF_8);
        requestBuffer = Unpooled.buffer(requestBytes.length);

        requestBuffer.writeBytes(requestBytes);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        // 在handler对象加载到pipeline的时候, 这个方法就会被毁掉

        System.out.println("调用handlerAdded");
    }


    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        // 把channel对象注册到EventLoop上的时候, 这个方法就会被回调

        System.out.println("调用channelRegistered");
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 通道被激活的时候, 也就是TCP连接建立成功的时候, 这个方法就会被回调

        System.out.println("调用channelActive");
        ctx.writeAndFlush(requestBuffer);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 读到了对方发来的数据的时候, 这个方法就会被回调

        System.out.println("调用channelRead");

        ByteBuf responseBuffer = (ByteBuf) msg;
        byte[] responseBytes = new byte[responseBuffer.readableBytes()];

        responseBuffer.readBytes(responseBytes);

        String response = new String(responseBytes, StandardCharsets.UTF_8);
        System.out.println("收到服务端响应: " + response);
    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // 读完对方发来的数据的时候, 这个方法就会被回调

        System.out.println("调用channelReadComplete");
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 当连接发生问题, 比如连接的另一方挂了，这个方法就会被回调

        System.out.println("调用channelInactive");
    }



    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        // 当channel从EventLoop上被取消注册的时候, 这个方法就会被回调

        System.out.println("调用channelUnregistered");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        // 当channel从pipeline中删除的时候, 这个方法就会被回调

        System.out.println("调用handlerRemoved");
    }




    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        System.out.println("调用userEventTriggered");
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {

        System.out.println("调用channelWritabilityChanged");
    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        System.out.println("调用exceptionCaught");
    }


}
