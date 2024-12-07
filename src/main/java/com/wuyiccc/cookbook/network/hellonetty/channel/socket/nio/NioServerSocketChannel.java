package com.wuyiccc.cookbook.network.hellonetty.channel.socket.nio;

import com.wuyiccc.cookbook.network.hellonetty.channel.ChannelConfig;
import com.wuyiccc.cookbook.network.hellonetty.channel.ChannelOption;
import com.wuyiccc.cookbook.network.hellonetty.channel.nio.AbstractNioMessageChannel;
import com.wuyiccc.cookbook.network.hellonetty.channel.nio.NioEventLoop;
import com.wuyiccc.cookbook.network.hellonetty.channel.socket.DefaultServerSocketChannelConfig;
import com.wuyiccc.cookbook.network.hellonetty.channel.socket.ServerSocketChannelConfig;
import com.wuyiccc.cookbook.network.hellonetty.util.internal.SocketUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.List;
import java.util.Map;

/**
 * @author wuyiccc
 * @date 2024/12/1 20:40
 * <p>
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

    private final ServerSocketChannelConfig config;

    public NioServerSocketChannel() {

        this(newSocket(DEFAULT_SELECTOR_PROVIDER));
    }

    public NioServerSocketChannel(ServerSocketChannel channel) {

        // 创建的为NioServerSocketChannel时, 没有父类channel, SelectionKey.OP_ACCEPT是服务端channel的关注事件
        super(null, channel, SelectionKey.OP_ACCEPT);
        config = new NioServerSocketChannelConfig(this, javaChannel().socket());
    }


    @Override
    public InetSocketAddress localAddress() {
        return (InetSocketAddress) super.localAddress();
    }


    @Override
    public ServerSocketChannelConfig config() {

        return config;
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
        javaChannel().bind(localAddress, config.getBacklog());

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


    private final class NioServerSocketChannelConfig extends DefaultServerSocketChannelConfig {
        private NioServerSocketChannelConfig(NioServerSocketChannel channel, ServerSocket javaSocket) {
            super(channel, javaSocket);
        }


        @Override
        public <T> boolean setOption(ChannelOption<T> option, T value) {
            if (option instanceof NioChannelOption) {
                //把用户设置的参数传入原生的jdk的channel中
                return NioChannelOption.setOption(jdkChannel(), (NioChannelOption<T>) option, value);
            }
            //正常调用的话，该方法的逻辑会走到这个分支处
            return super.setOption(option, value);
        }

        @Override
        public <T> T getOption(ChannelOption<T> option) {
            //这里有一行代码，判断jdk版本是否大于7，我就直接删掉了，默认大家用的都是7以上，否则要引入更多工具类
            if (option instanceof NioChannelOption) {
                return NioChannelOption.getOption(jdkChannel(), (NioChannelOption<T>) option);
            }
            return super.getOption(option);
        }

        @Override
        public Map<ChannelOption<?>, Object> getOptions() {
            return getOptions(super.getOptions(), NioChannelOption.getOptions(jdkChannel()));
        }

        private ServerSocketChannel jdkChannel() {
            return ((NioServerSocketChannel) channel).javaChannel();
        }
    }
}
