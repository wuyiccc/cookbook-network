package com.wuyiccc.cookbook.network.hellonetty.channel.nio;

import com.wuyiccc.cookbook.network.hellonetty.channel.AbstractChannel;
import com.wuyiccc.cookbook.network.hellonetty.channel.Channel;
import com.wuyiccc.cookbook.network.hellonetty.channel.ChannelPromise;
import com.wuyiccc.cookbook.network.hellonetty.channel.EventLoop;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * @author wuyiccc
 * @date 2024/12/1 15:36
 *
 * 专门负责处理nio模式下的数据, 可能有epoll等其他特殊模式的存在
 */
@Slf4j
public abstract class AbstractNioChannel extends AbstractChannel {

    // 该抽象类是ServerSocketChannel和SocketChannel的公共父类
    private final SelectableChannel ch;

    // channel要关注的事件
    protected final int readInterestOp;

    // channel注册刀selector返回的key
    volatile SelectionKey selectionKey;

    // 是否还有未读取的数据
    boolean readPending;


    protected AbstractNioChannel(Channel parent, SelectableChannel ch, int readInterestOp) {

        super(parent);
        this.ch = ch;
        this.readInterestOp = readInterestOp;

        try {
            // 设置服务端channel为非阻塞模式
            ch.configureBlocking(false);
        } catch (IOException e) {

            try {
                // 有异常直接关闭channel
                ch.close();
            } catch (IOException e2) {
                throw new RuntimeException(e2);
            }

            throw new RuntimeException("Failed to enter non-blocking mode.", e);
        }
    }


    @Override
    public boolean isOpen() {
        return ch.isOpen();
    }

    protected SelectableChannel javaChannel() {

        return ch;
    }


    @Override
    public NioEventLoop eventLoop() {

        return (NioEventLoop) super.eventLoop();
    }


    protected SelectionKey selectionKey() {

        assert selectionKey != null;

        return selectionKey;
    }


    @Override
    protected boolean isCompatible(EventLoop loop) {

        return loop instanceof NioEventLoop;
    }

    @Override
    protected void doRegister() throws Exception{

        // 在这里把channel注册到单线程执行器中的selector上, 注意这里的第三个参数this, 这意味着channel注册的时候把本身，也就是nio类的channel当做附件放到key上了
        selectionKey = javaChannel().register(eventLoop().unwrappedSelector(), 0, this);
    }

    @Override
    protected void doBeginRead() {

        final SelectionKey selectionKey = this.selectionKey;

        // 检查key是否是有效的
        if(!selectionKey.isValid()) {
            return;
        }

        // 还没有设置感兴趣的事件, 所以得到的值为0
        final int interestOps = selectionKey.interestOps();

        // 如果interestOps中并不包含readInterestOp, 那么注册对读事件的监听
        if ((interestOps & readInterestOp) == 0) {
            // 设置channel关注的事件, 读事件(新增)
            selectionKey.interestOps(interestOps | readInterestOp);
        }
    }


    @Override
    public final void connect(final SocketAddress remoteAddress, final SocketAddress localAddress, final ChannelPromise promise) {

        try {
            boolean doConnect = doConnect(remoteAddress, localAddress);
            if (!doConnect) {
                // TODO(wuyiccc): 这里代码会有一个bug, 后面修复, 在收发数据的时候体现出来
                promise.trySuccess();
            }
        } catch (Exception e) {
            log.error("connect异常", e);
        }

    }

    protected abstract boolean doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception;


    protected abstract void read();

}
