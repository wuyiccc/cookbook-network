package com.wuyiccc.cookbook.network.day13.demo02;

import io.netty.channel.EventLoopGroup;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * @author wuyiccc
 * @date 2024/11/17 20:24
 */
@Slf4j
public abstract class SingleThreadEventLoop extends SingleThreadEventExecutor {



    public void register(SocketChannel socketChannel, NioEventLoop nioEventLoop) {


        if (inEventLoop(Thread.currentThread())) {
            register0(socketChannel, nioEventLoop);
        } else {
            nioEventLoop.execute(() -> {
                register0(socketChannel, nioEventLoop);
                log.info("客户端channel已经注册到新线程的多路复用器上了!");
            });
        }

    }

    private void register0(SocketChannel channel, NioEventLoop nioEventLoop) {

        try {
            channel.configureBlocking(false);
            channel.register(nioEventLoop.selector(), SelectionKey.OP_READ);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
