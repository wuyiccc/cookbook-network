package com.wuyiccc.cookbook.network.hellonetty.channel.socket;

import com.wuyiccc.cookbook.network.hellonetty.channel.Channel;
import com.wuyiccc.cookbook.network.hellonetty.channel.nio.AbstractNioByteChannel;
import com.wuyiccc.cookbook.network.hellonetty.util.internal.SocketUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;

/**
 * @author wuyiccc
 * @date 2024/12/1 15:35
 */
@Slf4j
public class NioSocketChannel extends AbstractNioByteChannel {

    private static final SelectorProvider DEFAULT_SELECTOR_PROVIDER = SelectorProvider.provider();


    private static SocketChannel newSocket(SelectorProvider provider) {

        try {
            return provider.openSocketChannel();
        } catch (IOException e) {
            throw new RuntimeException("Failed to open a socket.", e);
        }
    }


    public NioSocketChannel() {

        this(DEFAULT_SELECTOR_PROVIDER);
    }

    public NioSocketChannel(SelectorProvider provider) {
        this(newSocket(provider));
    }


    public NioSocketChannel(SocketChannel socket) {

        this(null, socket);
    }


    public NioSocketChannel(Channel parent, SocketChannel socket) {

        super(parent, socket);
    }

    @Override
    protected SocketChannel javaChannel() {

        return (SocketChannel) super.javaChannel();
    }

    @Override
    public boolean isActive() {
        // channel是否为Connected状态, 是客户端channel判断是否激活的条件
        SocketChannel ch = javaChannel();
        return ch.isOpen() && ch.isConnected();
    }

    @Override
    protected void doBind(SocketAddress localAddress) throws Exception {

        doBind0(localAddress);
    }


    private void doBind0(SocketAddress localAddress) throws Exception {

        SocketUtils.bind(javaChannel(), localAddress);
    }

    @Override
    protected boolean doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {

        if (localAddress != null) {
            doBind0(localAddress);
        }

        boolean success = false;

        try {

            boolean connected = SocketUtils.connect(javaChannel(), remoteAddress);
            if (!connected) {
                selectionKey().interestOps(SelectionKey.OP_CONNECT);
            }
            success = true;
            return connected;
        } finally {

            if (!success) {
                doClose();
            }
        }
    }


    protected void doClose() throws Exception {

        javaChannel().close();
    }

    @Override
    protected int doReadBytes(ByteBuffer byteBuf) throws Exception {

        int len = javaChannel().read(byteBuf);
        byte[] buffer = new byte[len];
        byteBuf.flip();
        byteBuf.get(buffer);

        log.info(">>> 客户端收到消息: {}", new String(buffer));

        // 返回读取到的字节长度
        return len;
    }

}
