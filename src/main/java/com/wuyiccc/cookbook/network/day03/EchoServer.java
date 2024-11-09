package com.wuyiccc.cookbook.network.day03;

import com.sun.security.ntlm.Server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author wuyiccc
 * @date 2024/11/8 20:48
 */
public class EchoServer {

    public static void main(String[] args) {


        ServerSocketChannel serverSocketChannel;
        Selector selector;

        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(10091));
            serverSocketChannel.configureBlocking(false);
            selector = Selector.open();

            // 注册selector
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }


        while (true) {

            try {
                selector.select();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }

            Set<SelectionKey> readyKeys = selector.selectedKeys();

            Iterator<SelectionKey> iterator = readyKeys.iterator();
            while (iterator.hasNext()) {

                SelectionKey key = iterator.next();
                iterator.remove();
                try {
                    if (key.isAcceptable()) {

                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        System.out.println("server == preServer: " + (server == serverSocketChannel));
                        SocketChannel clientChannel = server.accept();
                        clientChannel.configureBlocking(false);

                        // 注册客户端监听事件
                        SelectionKey clientKey = clientChannel.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ);
                        // 构建byteBuffer
                        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
                        clientKey.attach(byteBuffer);
                    } else if (key.isReadable()) {
                        // 读取客户端传来的数据
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer output = (ByteBuffer) key.attachment();

                        // 读取到output中, 这个时候output可能还有上一次未写入完的数据, 这里直接写入的效果
                        // 是追加到buffer末尾, 不影响echo功能
                        client.read(output);
                    } else if (key.isWritable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer output = (ByteBuffer) key.attachment();
                        // 这里可能是上一次读取完毕, 或者是还有部分未写入的内容, 这里position直接在
                        // buffer的后面, flip翻转之后再次写入
                        output.flip();
                        // write方法是非阻塞的, 不一定全部将output的数据写完
                        client.write(output);
                        // 压缩数据, 将byteBuffer的数据整体前移
                        // 将position移动到压缩之后的空间末尾的下一个位置, limit=capacity
                        output.compact();
                        System.out.println(key);
                    }
                } catch (Exception e) {
                    key.cancel();
                    try {
                        key.channel().close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

            }
        }


    }
}
