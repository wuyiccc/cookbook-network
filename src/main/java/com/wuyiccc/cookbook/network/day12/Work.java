package com.wuyiccc.cookbook.network.day12;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

/**
 * @author wuyiccc
 * @date 2024/11/16 22:38
 */
public class Work implements Runnable {

    private final ServerSocketChannel serverSocketChannel;

    private final Selector selector = Selector.open();

    private final Thread thread;

    private final SelectionKey selectionKey;

    public Work(ServerSocketChannel serverSocketChannel) throws IOException {

        this.serverSocketChannel = serverSocketChannel;
        selectionKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        thread = new Thread(this);
    }

    public void start() {

        thread.start();
    }

    @Override
    public void run() {

        while (true) {

            try {

                selector.select();
            } catch (Exception e) {

                System.out.println("selector.select异常: " + e.getLocalizedMessage());
                continue;
            }

            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

            while (iterator.hasNext()) {

                try {
                    SelectionKey selectionKey = iterator.next();
                    iterator.remove();

                    if (selectionKey.isAcceptable()) {

                        ServerSocketChannel channel = (ServerSocketChannel) selectionKey.channel();
                        SocketChannel socketChannel = channel.accept();
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ);

                        socketChannel.write(ByteBuffer.wrap("我还是不netty, 但是我知道你上线了".getBytes(StandardCharsets.UTF_8)));


                    }

                    if (selectionKey.isReadable()) {

                        SocketChannel channel = (SocketChannel) selectionKey.channel();
                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                        int len = channel.read(byteBuffer);
                        if (len == -1) {
                            System.out.println("客户端通道要关闭了!");
                            channel.close();
                        }
                    }
                } catch (IOException e) {
                    selectionKey.cancel();
                    try {
                        selectionKey.channel().close();
                    } catch (IOException ex) {
                        System.out.println("关闭selectionKey#channel异常: " + ex.getLocalizedMessage());
                    }
                }
            }

        }

    }


}
