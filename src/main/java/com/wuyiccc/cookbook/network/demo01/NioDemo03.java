package com.wuyiccc.cookbook.network.demo01;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author wuyiccc
 * @date 2024/11/6 20:47
 */
public class NioDemo03 {

    public static void main(String[] args) throws IOException {

        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        //ServerSocket socket = serverChannel.socket();
        //socket.bind(new InetSocketAddress(19));
        serverChannel.bind(new InetSocketAddress(10019));

        // accept方法默认处于阻塞模式, 直到有一个客户端建立连接为止
        // 只有手动设置下面代码修改为非阻塞模式之后, 才不会阻塞, 非阻塞模式下, 如果没有客户端链接, accept会返回一个null
        serverChannel.configureBlocking(false);
        SocketChannel clientChannel = serverChannel.accept();
        // 设置客户端通道处于非阻塞模式
        clientChannel.configureBlocking(false);



    }
}
