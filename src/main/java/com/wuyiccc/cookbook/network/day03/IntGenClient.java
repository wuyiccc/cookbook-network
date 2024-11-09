package com.wuyiccc.cookbook.network.day03;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author wuyiccc
 * @date 2024/11/8 19:56
 */
public class IntGenClient {

    public static void main(String[] args) throws IOException {


        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("localhost", 10091));

        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        // 获取到视图缓冲区, intBuffer是基于byteBuffer对象的, intBuffer和byteBuffer的position和limit都是独立的
        // 但是底层数据是共享的
        IntBuffer intBuffer = byteBuffer.asIntBuffer();

        for (int expected = 0; ; expected++) {


            // 读取之前重置一下byteBuffer的参数
            byteBuffer.clear();

            // 阻塞读取
            socketChannel.read(byteBuffer);

            // 读取之前重置一下intBuffer的参数, 因为intBuffer的参数和byteBuffer的参数互不影响
            // 主要目的是将position设置为0
            intBuffer.clear();
            int actual = intBuffer.get();

            if (actual != expected) {
                System.err.println("expected " + expected + "; was " + actual);
                break;
            }
            System.out.println(actual);
        }


    }
}
