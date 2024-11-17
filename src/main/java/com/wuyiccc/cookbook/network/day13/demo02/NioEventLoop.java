package com.wuyiccc.cookbook.network.day13.demo02;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Set;

/**
 * @author wuyiccc
 * @date 2024/11/17 20:25
 */
@Slf4j
public class NioEventLoop extends SingleThreadEventLoop {


    private final SelectorProvider provider;

    private Selector selector;


    public NioEventLoop() {
        this.provider = SelectorProvider.provider();
        this.selector = openSelector();
    }


    private Selector openSelector() {
        try {

            return this.provider.openSelector();
        } catch (Exception e) {
            throw new RuntimeException("failed to open a new selector", e);
        }
    }



    public Selector selector() {
        return this.selector;
    }


    @Override
    protected void run() {

        while (true) {
            try {
                select();
                // 执行io事件
                processSelectedKeys(selector.selectedKeys());
            } catch (IOException e) {
                log.error("select异常", e);
            } finally {
                // 执行注册事件
                runAllTasks();
            }
        }
    }

    private void select() throws IOException {

        Selector selector = this.selector;

        for (;;) {
            log.info("新线程在这里阻塞3s吧...");
            int select = selector.select(3000);
            if (select != 0 || hasTasks()) {
                break;
            }
        }
    }

    private void processSelectedKeys(Set<SelectionKey> selectionKeys) throws IOException {

        if (selectionKeys.isEmpty()) {
            return;
        }

        Iterator<SelectionKey> iterator = selectionKeys.iterator();
        for (;;) {
            SelectionKey key = iterator.next();
            iterator.remove();
            processSelectedKey(key);
            if (!iterator.hasNext()) {
                break;
            }
        }
    }

    private void processSelectedKey(SelectionKey key) throws IOException {

        if (key.isReadable()) {
            SocketChannel socketChannel = (SocketChannel) key.channel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            int len = socketChannel.read(byteBuffer);
            if (len == -1) {
                log.warn("客户端通道要关闭!");
                socketChannel.close();
                return;
            }

            byte[] data = new byte[len];
            byteBuffer.flip();
            byteBuffer.get(data);
            log.info("新线程收到客户端发送的数据: {}", new String(data));
        }
    }

}
