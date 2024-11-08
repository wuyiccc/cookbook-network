package com.wuyiccc.cookbook.network.day02;

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
 * @date 2024/11/7 23:07
 */
public class demo06 {

    public static void main(String[] args) {


        ServerSocketChannel serverSocketChannel;
        Selector selector;

        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(10091));

            // server nio selector 模式必须设置为false
            serverSocketChannel.configureBlocking(false);
            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        while (true) {

            try {
                // 阻塞获取事件
                selector.select();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Set<SelectionKey> selectionKeyList = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeyList.iterator();

            while (iterator.hasNext()) {

                SelectionKey key = iterator.next();
                // 移除事件
                iterator.remove();

                try {

                    if (key.isAcceptable()) {
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        SocketChannel client = server.accept();
                        client.configureBlocking(false);
                        SelectionKey clientKey = client.register(selector, SelectionKey.OP_WRITE);
                        ByteBuffer output = ByteBuffer.allocate(4);
                        output.putInt(0);
                        output.flip();
                        clientKey.attach(output);
                    } else if (key.isWritable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer output = (ByteBuffer) key.attachment();
                        if (!output.hasRemaining()) {
                            // 重置position
                            output.rewind();
                            int value = output.getInt();
                            // 重置所有参数
                            output.clear();
                            output.putInt(value + 1);
                            output.flip();
                        }
                        client.write(output);
                    }
                } catch (Exception e)  {
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
