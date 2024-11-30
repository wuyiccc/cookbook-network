package com.wuyiccc.cookbook.network.hellonetty.bootstrap;

import com.wuyiccc.cookbook.network.hellonetty.channel.EventLoopGroup;
import com.wuyiccc.cookbook.network.hellonetty.channel.nio.NioEventLoop;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

/**
 * @author wuyiccc
 * @date 2024/11/22 22:48
 */
@Slf4j
public class Bootstrap {


    private NioEventLoop nioEventLoop;

    private SocketChannel socketChannel;

    private EventLoopGroup workerGroup;

    public Bootstrap() {

    }

    public Bootstrap group(EventLoopGroup eventLoopGroup) {
        this.workerGroup = eventLoopGroup;
        return this;
    }

    public Bootstrap socketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
        return this;
    }

    public void connect(String inetHost, int inetPort) {
        connect(new InetSocketAddress(inetHost, inetPort));
    }

    public void connect(SocketAddress localAddress) {
        doConnect(localAddress);
    }

    private void doConnect(SocketAddress localAddress) {

        nioEventLoop = (NioEventLoop) workerGroup.next().next();
        nioEventLoop.setSocketChannel(socketChannel);
        nioEventLoop.register(socketChannel, nioEventLoop);
        doConnect0(localAddress);
    }


    private void doConnect0(SocketAddress localAddress) {

        nioEventLoop.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    socketChannel.connect(localAddress);
                    log.info(">>> 客户端channel连接服务器成功了");
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        });
    }

}
