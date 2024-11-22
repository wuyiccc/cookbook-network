package com.wuyiccc.cookbook.network.day14.core.bootstrap;

import com.wuyiccc.cookbook.network.day14.core.event.EventLoopGroup;
import com.wuyiccc.cookbook.network.day14.core.event.NioEventLoop;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;

/**
 * @author wuyiccc
 * @date 2024/11/22 22:53
 */
@Slf4j
public class ServerBootstrap {


    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;

    private NioEventLoop nioEventLoop;

    private ServerSocketChannel serverSocketChannel;

    public ServerBootstrap() {

    }

    public ServerBootstrap group(EventLoopGroup parentGroup, EventLoopGroup childGroup) {
        this.bossGroup = parentGroup;
        this.workerGroup = childGroup;
        return this;
    }

    public ServerBootstrap serverSocketChannel(ServerSocketChannel serverSocketChannel) {

        this.serverSocketChannel = serverSocketChannel;
        return this;
    }

    public void bind(String host, int inetPort) {

        bind(new InetSocketAddress(host, inetPort));
    }

    public void bind(SocketAddress localAddress) {

        doBind(localAddress);
    }

    private void doBind(SocketAddress localAddress) {

        //得到boss事件循环组中的事件执行器，也就是单线程执行器,这个里面其实就包含一个单线程执行器，在workergroup中才包含多个单线程执行器
        //这里就暂时先写死了
        nioEventLoop = (NioEventLoop) bossGroup.next().next();
        nioEventLoop.setServerSocketChannel(serverSocketChannel);
        nioEventLoop.setWorkerGroup(workerGroup);
        //直接使用nioeventloop把服务端的channel注册到单线程执行器上
        nioEventLoop.register(serverSocketChannel, nioEventLoop);
        doBind0(localAddress);
    }


    private void doBind0(SocketAddress localAddress) {
        nioEventLoop.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    serverSocketChannel.bind(localAddress);
                    log.info("服务端channel绑定了服务器端口了");
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        });


    }

}