package com.wuyiccc.cookbook.network.day12.demo02;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class TestServer {


    public static void main(String[] args) throws Exception {

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        //创建新的线程
        Work work = new Work();
        //得到work中的selector
        Selector workSelector = work.getSelector();

        Selector selector = Selector.open();
        SelectionKey selectionKey = serverSocketChannel.register(selector, 0, serverSocketChannel);
        selectionKey.interestOps(SelectionKey.OP_ACCEPT);
        serverSocketChannel.bind(new InetSocketAddress(8080));

        while (true) {

            System.out.println("main函数阻塞在这里吧...");
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectionKeys.iterator();
            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                keyIterator.remove();
                if (key.isAcceptable()) {
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    //得到客户端的channel
                    SocketChannel socketChannel = channel.accept();
                    socketChannel.configureBlocking(false);
                    //把客户端的channel注册到新线程的selector上，但这时，新的线程还未启动
                    SelectionKey socketChannelKey = socketChannel.register(workSelector, 0, socketChannel);
                    //给客户端的channel设置可读事件
                    socketChannelKey.interestOps(SelectionKey.OP_READ);
                    //可以启动新的线程了,在while循环中，我们必须保证线程只启动一次
                    work.start();
                    System.out.println("客户端在main函数中连接成功!");
                    //连接成功之后，用客户端的channel写回一条消息
                    socketChannel.write(ByteBuffer.wrap("客户端发送成功了".getBytes()));
                    System.out.println("main函数服务器向客户端发送数据成功");
                }
            }
        }
    }
}