package com.wuyiccc.cookbook.network.hellonetty.channel.socket.nio;

import com.wuyiccc.cookbook.network.hellonetty.channel.Channel;
import com.wuyiccc.cookbook.network.hellonetty.channel.ChannelOption;
import com.wuyiccc.cookbook.network.hellonetty.channel.nio.AbstractNioByteChannel;
import com.wuyiccc.cookbook.network.hellonetty.channel.socket.DefaultSocketChannelConfig;
import com.wuyiccc.cookbook.network.hellonetty.channel.socket.SocketChannelConfig;
import com.wuyiccc.cookbook.network.hellonetty.util.internal.SocketUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Map;

/**
 * @author wuyiccc
 * @date 2024/12/1 15:35
 * <p>
 * 客户端channel
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

    private final SocketChannelConfig config;


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
        config = new NioSocketChannelConfig(this, socket.socket());
    }

    @Override
    public SocketChannelConfig config() {
        return config;
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
    public InetSocketAddress localAddress() {
        return (InetSocketAddress) super.localAddress();
    }

    @Override
    public InetSocketAddress remoteAddress() {
        return (InetSocketAddress) super.remoteAddress();
    }

    @Override
    protected SocketAddress localAddress0() {

        return javaChannel().socket().getLocalSocketAddress();
    }

    @Override
    protected SocketAddress remoteAddress0() {

        return javaChannel().socket().getRemoteSocketAddress();
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

    @Override
    protected void doFinishConnect() throws Exception {

        if (!javaChannel().finishConnect()) {
            throw new Error();
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

    @Override
    protected void doWrite(Object msg) throws Exception {

        SocketChannel socketChannel = javaChannel();

        ByteBuffer buffer = (ByteBuffer) msg;
        socketChannel.write(buffer);
        log.info(">>> 客户端发送数据成功了");
    }

    private final class NioSocketChannelConfig extends DefaultSocketChannelConfig {

        // 本次 write loop 最大允许发送的字节数
        private volatile int maxBytesPerGatheringWrite = Integer.MAX_VALUE;
        private NioSocketChannelConfig(NioSocketChannel channel, Socket javaSocket) {

            super(channel, javaSocket);
            calculateMaxBytesPerGatheringWrite();
        }

        @Override
        public NioSocketChannelConfig setSendBufferSize(int sendBufferSize) {
            super.setSendBufferSize(sendBufferSize);
            calculateMaxBytesPerGatheringWrite();
            return this;
        }

        @Override
        public <T> boolean setOption(ChannelOption<T> option, T value) {
            if ( option instanceof NioChannelOption) {
                return NioChannelOption.setOption(jdkChannel(), (NioChannelOption<T>) option, value);
            }
            return super.setOption(option, value);
        }

        @Override
        public <T> T getOption(ChannelOption<T> option) {
            if (option instanceof NioChannelOption) {
                return NioChannelOption.getOption(jdkChannel(), (NioChannelOption<T>) option);
            }
            return super.getOption(option);
        }

        @Override
        public Map<ChannelOption<?>, Object> getOptions() {
            return getOptions(super.getOptions(), NioChannelOption.getOptions(jdkChannel()));
        }

        void setMaxBytesPerGatheringWrite(int maxBytesPerGatheringWrite) {
            this.maxBytesPerGatheringWrite = maxBytesPerGatheringWrite;
        }

        int getMaxBytesPerGatheringWrite() {
            return maxBytesPerGatheringWrite;
        }

        private void calculateMaxBytesPerGatheringWrite() {

            int newSendBufferSize = getSendBufferSize() << 1;
            if (newSendBufferSize > 0) {
                setMaxBytesPerGatheringWrite(getSendBufferSize() << 1);
            }
        }

        private SocketChannel jdkChannel() {
            return ((NioSocketChannel) channel).javaChannel();
        }
    }
}
