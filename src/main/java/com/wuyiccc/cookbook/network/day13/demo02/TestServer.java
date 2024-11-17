package com.wuyiccc.cookbook.network.day13.demo02;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

/**
 * @author wuyiccc
 * @date 2024/11/17 21:02
 */
@Slf4j
public class TestServer {

    public static void main(String[] args) throws IOException {

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(8080));
        serverSocketChannel.configureBlocking(false);

        Selector selector = Selector.open();
        SelectionKey selectionKey = serverSocketChannel.register(selector, 0, serverSocketChannel);
        selectionKey.interestOps(SelectionKey.OP_ACCEPT);

        NioEventLoop[] workerGroup = new NioEventLoop[2];
        workerGroup[0] = new NioEventLoop();
        workerGroup[1] = new NioEventLoop();

        int i = 0;

        while (true) {
            log.info("main函数阻塞中...");
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isAcceptable()) {
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel socketChannel = channel.accept();
                    int index = i % workerGroup.length;
                    workerGroup[index].register(socketChannel, workerGroup[index]);
                    i++;
                    log.info("socketChannel注册到了第{}个单线程执行器上", index);

                    socketChannel.write(ByteBuffer.wrap("服务端发送成功率".getBytes(StandardCharsets.UTF_8)));
                }
            }
        }

    }
}
