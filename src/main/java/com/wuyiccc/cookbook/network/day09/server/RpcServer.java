package com.wuyiccc.cookbook.network.day09.server;

import com.wuyiccc.cookbook.network.day09.handler.coder.RpcDecoder;
import com.wuyiccc.cookbook.network.day09.handler.coder.RpcEncoder;
import com.wuyiccc.cookbook.network.day09.protocol.request.RpcRequest;
import com.wuyiccc.cookbook.network.day09.protocol.response.RpcResponse;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author wuyiccc
 * @date 2024/11/15 22:21
 */
public class RpcServer {


    private static final int DEFAULT_SERVER_PORT = 8998;


    private static List<ServiceConfig> serviceConfigList = new CopyOnWriteArrayList<>();


    public static void start() throws InterruptedException {

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {

                        ch.pipeline().addLast(new RpcDecoder(RpcRequest.class));
                        ch.pipeline().addLast(new RpcEncoder(RpcResponse.class));
                        ch.pipeline().addLast(new RpcServiceHandler(serviceConfigList));
                    }
                });

        ChannelFuture cf = serverBootstrap.bind(DEFAULT_SERVER_PORT).sync();
        cf.channel().closeFuture().sync();

        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    public static void main(String[] args) throws InterruptedException {

        ServiceConfig serviceConfig = new ServiceConfig("TestService", TestService.class, TestServiceImpl.class);

        serviceConfigList.add(serviceConfig);


        start();

    }
}
