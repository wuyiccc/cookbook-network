package com.wuyiccc.cookbook.network.day14.test;


import com.wuyiccc.cookbook.network.day14.core.bootstrap.ServerBootstrap;
import com.wuyiccc.cookbook.network.day14.core.event.NioEventLoopGroup;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;

public class ServerTest {
    public static void main(String[] args) throws IOException {

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(2);
        serverBootstrap.group(bossGroup, workerGroup).
                serverSocketChannel(serverSocketChannel);

        serverBootstrap.bind("127.0.0.1", 8080);
    }
}
