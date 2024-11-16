package com.wuyiccc.cookbook.network.day11;

import com.wuyiccc.cookbook.network.day10.HttpServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @author wuyiccc
 * @date 2024/11/16 11:31
 */
public class WebSocketServer {


    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup parentGroup = new NioEventLoopGroup();
        EventLoopGroup childGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(parentGroup, childGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {



                        ch.pipeline().addLast(new HttpServerCodec());
                        ch.pipeline().addLast(new ChunkedWriteHandler());
                        ch.pipeline().addLast(new HttpObjectAggregator(1024 * 32));
                        ch.pipeline().addLast(new WebSocketServerProtocolHandler("/websocket"));
                        ch.pipeline().addLast("netty-websocket-server-handler", new WebSocketServerHandler());
                    }
                });

        ChannelFuture cf = serverBootstrap.bind(8080).sync();


        cf.channel().closeFuture().addListener((ChannelFutureListener) future -> {

            parentGroup.shutdownGracefully();
            childGroup.shutdownGracefully();
        });
    }
}
