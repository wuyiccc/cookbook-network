package com.wuyiccc.cookbook.network.hellonetty.test;


import com.wuyiccc.cookbook.network.hellonetty.bootstrap.Bootstrap;
import com.wuyiccc.cookbook.network.hellonetty.channel.Channel;
import com.wuyiccc.cookbook.network.hellonetty.channel.ChannelFuture;
import com.wuyiccc.cookbook.network.hellonetty.channel.nio.NioEventLoopGroup;
import com.wuyiccc.cookbook.network.hellonetty.channel.socket.nio.NioSocketChannel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ClientTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(1);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup).
                channel(NioSocketChannel.class);
        ChannelFuture cf = bootstrap.connect("127.0.0.1", 8080).sync();
    }

}
