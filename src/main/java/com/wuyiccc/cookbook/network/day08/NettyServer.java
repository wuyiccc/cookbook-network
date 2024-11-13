package com.wuyiccc.cookbook.network.day08;

import com.wuyiccc.cookbook.network.day06.NettyServerHandler;
import com.wuyiccc.cookbook.network.day08.handler.*;
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
        EventLoopGroup parentGroup = new NioEventLoopGroup();
        EventLoopGroup childGroup = new NioEventLoopGroup();

        try {

            // 初始化服务器
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap.group(parentGroup, childGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {

                            // iA -> iB -> iC -> oA -> oB -> oC

                            //ch.pipeline().addLast(new InBoundHandlerA());
                            //ch.pipeline().addLast(new InBoundHandlerB());
                            //ch.pipeline().addLast(new InBoundHandlerC());



                            //ch.pipeline().addLast(new OutBoundHandlerC());
                            //ch.pipeline().addLast(new OutBoundHandlerB());
                            //ch.pipeline().addLast(new OutBoundHandlerA());


                            //ch.pipeline().addLast(new InBoundHandlerA());
                            //ch.pipeline().addLast(new OutBoundHandlerC());
                            //ch.pipeline().addLast(new InBoundHandlerB());
                            //ch.pipeline().addLast(new InBoundHandlerC());


                            //ch.pipeline().addLast(new OutBoundHandlerB());
                            //ch.pipeline().addLast(new OutBoundHandlerA());

                            ch.pipeline().addLast(new RouterServerHandler());

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
