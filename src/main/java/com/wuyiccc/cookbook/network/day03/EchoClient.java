package com.wuyiccc.cookbook.network.day03;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;

/**
 * @author wuyiccc
 * @date 2024/11/8 23:57
 */
public class EchoClient {

    public static void main(String[] args) throws IOException {


        SocketAddress rama = new InetSocketAddress("localhost", 10091);

        // 通道以阻塞模式打开, 只有在真正建立连接之后才会执行后续的代码
        SocketChannel socketChannel = SocketChannel.open(rama);


        ByteBuffer byteBuffer = ByteBuffer.allocate(5);


        ByteBuffer dataBuffer = ByteBuffer.allocate(20);
        dataBuffer.put((byte) 1);
        dataBuffer.put((byte) 2);
        dataBuffer.put((byte) 3);
        dataBuffer.put((byte) 4);
        dataBuffer.put((byte) 5);
        dataBuffer.put((byte) 6);
        dataBuffer.put((byte) 7);
        dataBuffer.put((byte) 8);

        dataBuffer.flip();

        socketChannel.write(dataBuffer);

        WritableByteChannel output = Channels.newChannel(System.out);

        int n = socketChannel.read(byteBuffer);
        while (n > -1) {

            byteBuffer.flip();

            output.write(byteBuffer);

            byteBuffer.clear();

            n = socketChannel.read(byteBuffer);
        }

    }
}
