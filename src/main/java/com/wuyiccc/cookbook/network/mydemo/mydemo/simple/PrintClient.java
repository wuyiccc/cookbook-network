package com.wuyiccc.cookbook.network.mydemo.mydemo.simple;

import com.wuyiccc.cookbook.network.mydemo.mydemo.simple.handler.ClientInputHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author wuyiccc
 * @date 2025/1/19 13:48
 */
public class PrintClient {

    public static void main(String[] args) throws InterruptedException {

        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();

        Bootstrap b = new Bootstrap();
        b.group(nioEventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {

                        ch.pipeline().addLast(new ClientInputHandler());
                    }
                });

        ChannelFuture f = b.connect("localhost", 10023).sync();

        f.channel().closeFuture().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {

                nioEventLoopGroup.shutdownGracefully();
            }
        });

    }
}
