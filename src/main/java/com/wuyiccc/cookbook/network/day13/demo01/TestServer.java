package com.wuyiccc.cookbook.network.day13.demo01;

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
import java.util.Set;

/**
 * @author wuyiccc
 * @date 2024/11/17 15:21
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

        SingleThreadEventExecutor singleThreadEventExecutor = new SingleThreadEventExecutor();

        while (true) {

            log.info("main函数阻塞在这里吧......");
            selector.select();

            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();

            while (iterator.hasNext()) {

                SelectionKey key = iterator.next();
                iterator.remove();

                if (key.isAcceptable()) {

                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    // 得到客户端的channel
                    SocketChannel socketChannel = channel.accept();
                    // 把客户端的channel注册到新线程的selector上
                    singleThreadEventExecutor.register(socketChannel);
                    log.info("客户端在main函数中连接成功!");
                    // 连接成功之后, 用客户端的channel写回一条消息
                    socketChannel.write(ByteBuffer.wrap("我发送成功了".getBytes(StandardCharsets.UTF_8)));
                    log.info("main函数服务器向客户端发送数据成功!");
                }
            }
        }

    }
}
