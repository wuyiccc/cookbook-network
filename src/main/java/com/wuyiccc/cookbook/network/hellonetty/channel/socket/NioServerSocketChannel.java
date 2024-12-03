package com.wuyiccc.cookbook.network.hellonetty.channel.socket;

import com.wuyiccc.cookbook.network.hellonetty.channel.nio.AbstractNioMessageChannel;
import com.wuyiccc.cookbook.network.hellonetty.channel.nio.NioEventLoop;
import com.wuyiccc.cookbook.network.hellonetty.util.internal.SocketUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.List;

/**
 * @author wuyiccc
 * @date 2024/12/1 20:40
 *
 * 服务端channel
 */
@Slf4j
public class NioServerSocketChannel extends AbstractNioMessageChannel {


    // 在无参构造器被调用的时候, 该成员变量就被创建了
    private static final SelectorProvider DEFAULT_SELECTOR_PROVIDER = SelectorProvider.provider();


    private static ServerSocketChannel newSocket(SelectorProvider provider) {

        try {
            return provider.openServerSocketChannel();
        } catch (IOException e) {
            throw new RuntimeException("Failed to open a server socket.", e);
        }
    }


    public NioServerSocketChannel() {

        this(newSocket(DEFAULT_SELECTOR_PROVIDER));
    }

    public NioServerSocketChannel(ServerSocketChannel channel) {

        // 创建的为NioServerSocketChannel时, 没有父类channel, SelectionKey.OP_ACCEPT是服务端channel的关注事件
        super(null, channel, SelectionKey.OP_ACCEPT);
    }


    @Override
    public InetSocketAddress localAddress() {
        return (InetSocketAddress) super.localAddress();
    }



    @Override
    public boolean isActive() {

        return isOpen() && javaChannel().socket().isBound();
    }

    @Override
    public SocketAddress remoteAddress() {

        return null;
    }

    @Override
    protected ServerSocketChannel javaChannel() {

        return (ServerSocketChannel) super.javaChannel();
    }

    @Override
    protected SocketAddress localAddress0() {

        return SocketUtils.localSocketAddress(javaChannel().socket());
    }

    @Override
    public NioEventLoop eventLoop() {

        return (NioEventLoop) super.eventLoop();
    }




    @Override
    protected void doBind(SocketAddress localAddress) throws Exception {

        // 这里暂时写死backlog等待队列长度为128
        javaChannel().bind(localAddress, 128);

        if (isActive()) {
            log.info(">>> 服务端绑定端口成功");
            doBeginRead();
        }
    }

    @Override
    protected void doClose() throws Exception {

        javaChannel().close();
    }


    // 该方法是服务端接收客户端channel连接的方法
    @Override
    protected int doReadMessages(List<Object> buf) throws Exception {


        // 有连接进来, 创建出java原生的客户端channel
        SocketChannel ch = SocketUtils.accept(javaChannel());

        try {

            if (ch != null) {

                // 创建nioSocketChannel
                buf.add(new NioSocketChannel(this, ch));
                return 1;
            }
        } catch (Throwable t) {

            log.error(">>> 异常", t);

            try {

                // 有异常则关闭channel客户端, 会自动关闭对应的SelectionKey
                ch.close();
            } catch (Throwable t2) {

                throw new RuntimeException("Failed to close a socket.", t);
            }
        }
        return 0;
    }

    @Override
    protected boolean doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {

        throw new UnsupportedOperationException();
    }

    @Override
    protected void doFinishConnect() throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected SocketAddress remoteAddress0() {
        return null;
    }

}
