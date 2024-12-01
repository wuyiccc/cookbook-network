package com.wuyiccc.cookbook.network.hellonetty.test;


import com.wuyiccc.cookbook.network.hellonetty.bootstrap.Bootstrap;
import com.wuyiccc.cookbook.network.hellonetty.channel.nio.NioEventLoopGroup;
import com.wuyiccc.cookbook.network.hellonetty.channel.socket.NioSocketChannel;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public class ClientTest {
    public static void main(String[] args) throws IOException {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(1);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup).
                channel(NioSocketChannel.class);
        bootstrap.connect("127.0.0.1",8080);
    }

}
