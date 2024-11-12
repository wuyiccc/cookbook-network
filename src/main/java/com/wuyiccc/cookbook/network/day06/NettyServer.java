package com.wuyiccc.cookbook.network.day06;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.UUID;
import java.util.concurrent.ThreadFactory;

/**
 * @author wuyiccc
 * @date 2024/11/10 14:52
 */
public class NettyServer {

    public static void main(String[] args) {


        // 分别创建两个处理网络的EventLoopGroup
        EventLoopGroup parentGroup = new NioEventLoopGroup(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {

                Thread t = new Thread(r);
                t.setName("parentGroup-" + UUID.randomUUID().toString());
                return t;
            }

        });
        EventLoopGroup childGroup = new NioEventLoopGroup(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {

                Thread t = new Thread(r);
                t.setName("childGroup-" + UUID.randomUUID().toString());
                return t;
            }
        });

        try {

            // 初始化服务器
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap.group(parentGroup, childGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            ch.pipeline().addLast(new NettyServerHandler());
                        }
                    });

            System.out.println("Server 启动了");

            ChannelFuture channelFuture = serverBootstrap.bind(10091).sync();

            // 同步等待服务器关闭
            channelFuture.channel().closeFuture().sync();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            parentGroup.shutdownGracefully();
            childGroup.shutdownGracefully();
        }

    }
}
