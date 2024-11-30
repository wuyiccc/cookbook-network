package com.wuyiccc.cookbook.network.hellonetty.test;


import com.wuyiccc.cookbook.network.hellonetty.bootstrap.Bootstrap;
import com.wuyiccc.cookbook.network.hellonetty.channel.nio.NioEventLoopGroup;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public class ClientTest {
    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(1);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup).
                socketChannel(socketChannel);
        bootstrap.connect("127.0.0.1",8080);
    }

}
