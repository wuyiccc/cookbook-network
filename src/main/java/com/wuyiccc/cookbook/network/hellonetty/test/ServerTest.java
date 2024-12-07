package com.wuyiccc.cookbook.network.hellonetty.test;


import com.wuyiccc.cookbook.network.hellonetty.bootstrap.ServerBootstrap;
import com.wuyiccc.cookbook.network.hellonetty.channel.ChannelOption;
import com.wuyiccc.cookbook.network.hellonetty.channel.nio.NioEventLoopGroup;
import com.wuyiccc.cookbook.network.hellonetty.channel.socket.nio.NioServerSocketChannel;
import com.wuyiccc.cookbook.network.hellonetty.util.concurrent.Future;
import com.wuyiccc.cookbook.network.hellonetty.util.concurrent.GenericFutureListener;

import java.io.IOException;

public class ServerTest {
    public static void main(String[] args) throws IOException {

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(2);
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 10)
                .bind("127.0.0.1", 8080)
                .addListener(new GenericFutureListener<Future<? super Object>>() {
                    @Override
                    public void operationComplete(Future<? super Object> future) throws Exception {
                        System.out.println("我绑定成功了");
                    }
                });
    }
}
