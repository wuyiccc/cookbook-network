package com.wuyiccc.cookbook.network.day04;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author wuyiccc
 * @date 2024/11/9 16:02
 */
public class SocketChannelTest {

    public static void main(String[] args) throws IOException {


        demo03();
    }

    public static void demo01() throws IOException {

        // 链接服务器方式1
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("localhost", 10091));

    }

    public static void demo02() throws IOException {

        // 链接服务器方式2
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("localhost", 10091));
    }

    public static void demo03() throws IOException {

        // 链接服务器方式3: 非阻塞方式
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);

        // connect的可能还没有建立连接就返回了
        boolean connect = socketChannel.connect(new InetSocketAddress("www.baidu.com", 80));

        // 非阻塞情况下, 返回true代表连接可用, false代表链接不可用, 如果链接过程中发生故障, 那么抛出异常
        boolean finishConnect = socketChannel.finishConnect();


        // 连接打开返回true
        boolean connected = socketChannel.isConnected();

        // 链接尚未打开返回true
        boolean connectionPending = socketChannel.isConnectionPending();


        socketChannel.write(new ByteBuffer[]{ByteBuffer.allocate(100), ByteBuffer.allocate(100)});
    }
}
