package com.wuyiccc.cookbook.network.day12.demo02;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class Work implements Runnable {


    private boolean flags;

    private final Selector selector = Selector.open();

    private final Thread thread;

    public Work() throws IOException {

        thread = new Thread(this);
    }

    public Selector getSelector() {

        return selector;
    }


    public void start() {

        if (flags) {
            return;
        }
        flags = true;
        thread.start();
    }

    @Override
    public void run() {
        while (true) {
            System.out.println("新线程阻塞在这里吧...");
            try {
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    iterator.remove();
                    if (selectionKey.isReadable()) {
                        SocketChannel channel = (SocketChannel) selectionKey.channel();
                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                        int len = channel.read(byteBuffer);
                        if (len == -1) {
                            System.out.println("客户端通道要关闭");
                            channel.close();
                            break;
                        }
                        byte[] bytes = new byte[len];
                        byteBuffer.flip();
                        byteBuffer.get(bytes);
                        System.out.println("新线程收到客户端发送的数据: " + new String(bytes, StandardCharsets.UTF_8));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
