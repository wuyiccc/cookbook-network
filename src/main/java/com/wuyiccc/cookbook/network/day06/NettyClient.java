package com.wuyiccc.cookbook.network.day06;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author wuyiccc
 * @date 2024/11/10 15:31
 */
public class NettyClient {

    public static void main(String[] args) {

        EventLoopGroup parent = new NioEventLoopGroup();

        try {

            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(parent)
                    .channel(NioSocketChannel.class)
                    // 高实时性配置为true
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {

                            //ch.pipeline().addLast(new NettyClientHandler());
                            ch.pipeline().addLast(new HandleLifeCycle());
                        }
                    });

            ChannelFuture channelFuture = bootstrap.connect("localhost", 10091).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
