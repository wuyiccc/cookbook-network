package com.wuyiccc.cookbook.network.mydemo.mydemo.demo01;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NioServer {

    public static void main(String[] args) throws InterruptedException {


        NioEventLoopGroup main = new NioEventLoopGroup();
        NioEventLoopGroup sub = new NioEventLoopGroup();


        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(main, sub)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 100)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {

                        ch.pipeline().addLast(new MyServerHandler());
                    }
                });

        ChannelFuture f = serverBootstrap.bind(10023).sync();

        f.channel().closeFuture().sync();

        main.shutdownGracefully();
        sub.shutdownGracefully();
    }
}
