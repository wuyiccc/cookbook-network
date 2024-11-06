package com.wuyiccc.cookbook.network.demo01;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author wuyiccc
 * @date 2024/11/6 21:17
 */
public class NioDemo04 {

    public static void main(String[] args) {


        byte[] rotation = new byte[95 * 2];
        for (byte i = ' '; i <= '~'; i++) {
            rotation[i - ' '] = i;
            rotation[i + 95 - ' '] = i;
        }

        ServerSocketChannel serverSocketChannel;
        Selector selector;

        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(10091));
            serverSocketChannel.configureBlocking(false);
            selector = Selector.open();
            // 只有serverSocketChannel为非阻塞模式的情况下才能register
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        while (true) {
            try {
                // 阻塞直到有新的事件产生
                int select = selector.select();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Set<SelectionKey> readKeys = selector.selectedKeys();

            Iterator<SelectionKey> iterator = readKeys.iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();

                // 移除SelectionKey, 避免下次重复调用
                iterator.remove();

                try {
                    if (key.isAcceptable()) {
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        SocketChannel client = server.accept();
                        System.out.println("Accepted connection from " + client);
                        client.configureBlocking(false);
                        // 如果客户端没有设置非阻塞模式, 下面register代码则会报错
                        SelectionKey clientKey = client.register(selector, SelectionKey.OP_WRITE);
                        ByteBuffer buffer = ByteBuffer.allocate(74);
                        buffer.put(rotation, 0, 72);
                        buffer.put((byte) '\r');
                        buffer.put((byte) '\n');
                        buffer.flip();
                        clientKey.attach(buffer);
                    } else if (key.isWritable()) {
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        ByteBuffer byteBuffer = (ByteBuffer) key.attachment();
                        if (!byteBuffer.hasRemaining()) {
                            // 如果byteBuffer的数据已经全部写入到client了
                            // 重置position
                            byteBuffer.rewind();
                            // 拿到上一次的首字符
                            byte first = byteBuffer.get();
                            // 再次重置position, 前面get之后position会加1
                            byteBuffer.rewind();
                            // 新的首字符的位置在原来的首字符的位置上+1
                            int newPosition = first - ' ' + 1;
                            byteBuffer.put(rotation, newPosition, 72);
                            byteBuffer.put((byte) '\r');
                            byteBuffer.put((byte) '\n');

                            // 调整为读模式
                            byteBuffer.flip();
                        }
                        socketChannel.write(byteBuffer);
                    }
                } catch (Exception e) {
                    key.cancel();
                    try {
                        key.channel().close();
                    } catch (IOException cex) {
                        throw new RuntimeException(cex);
                    }
                }

            }

        }

    }
}
