package com.wuyiccc.cookbook.network.hellonetty.channel;

import com.wuyiccc.cookbook.network.day03.EchoClient;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;

/**
 * @author wuyiccc
 * @date 2024/11/30 22:13
 */
@Slf4j
public abstract class AbstractChannel implements Channel {

    /**
     * 如果创建的为服务端channel, parent则为null
     */
    private final Channel parent;


    private final ChannelId id;


    private final CloseFuture closeFuture = new CloseFuture(this);

    private volatile SocketAddress localAddress;

    private volatile SocketAddress remoteAddress;

    private Throwable initialCloseCause;

    private volatile EventLoop eventLoop;


    private volatile boolean registered;


    protected AbstractChannel(Channel parent) {
        this.parent = parent;
        id = newId();
    }

    protected AbstractChannel(Channel parent, ChannelId id) {

        this.parent = parent;
        this.id = id;
    }

    @Override
    public final ChannelId id() {

        return id;
    }

    @Override
    public EventLoop eventLoop() {

        EventLoop eventLoop = this.eventLoop;
        if (eventLoop == null) {
            throw new IllegalStateException("channel not registered to an event loop");
        }
        return eventLoop;
    }

    @Override
    public Channel parent() {
        return parent;
    }

    @Override
    public ChannelConfig config() {
        return null;
    }

    @Override
    public boolean isRegistered() {

        return registered;
    }

    @Override
    public SocketAddress localAddress() {

        return null;
    }

    @Override
    public SocketAddress remoteAddress() {

        return null;
    }

    @Override
    public ChannelFuture closeFuture() {

        return closeFuture;
    }


    @Override
    public ChannelFuture close() {

        return null;
    }


    protected ChannelId newId() {

        return DefaultChannelId.newInstance();
    }


    protected abstract boolean isCompatible(EventLoop loop);

    @Override
    public final void register(EventLoop eventLoop, final ChannelPromise promise) {

        if (eventLoop == null) {
            throw new NullPointerException("eventLoop");
        }

        // 检查channel是否注册过, 注册过就手动设置promise失败
        if (isRegistered()) {

            promise.setFailure(new IllegalStateException("registered to an event loop already"));
            return;
        }

        // 判断党群使用的执行器是否是NioEventLoop, 如果不是, 就手动设置为失败
        if (!isCompatible(eventLoop)) {
            promise.setFailure(new IllegalStateException("incompatible event loop type: " + eventLoop.getClass().getName()));
            return;
        }

        // 一个channel绑定一个单线程执行器
        // 接着不管是客户端还是服务端都会把自己注册到绑定的单线程执行器中的selector中
        AbstractChannel.this.eventLoop = eventLoop;

        // 在netty中, channel注册, 绑定, 连接等等都是异步的, 由单线程执行器来执行
        if (eventLoop.inEventLoop(Thread.currentThread())) {

            register0(promise);
        } else {
            try {

                eventLoop.execute(new Runnable() {
                    @Override
                    public void run() {

                        register0(promise);
                    }
                });
            } catch (Throwable t) {

                log.info(t.getMessage());
                // 该方法先不做实现, 等引入unsafe之后会实现
                closeFuture.setClosed();
                safeSetFailure(promise, t);
            }
        }
    }

    private void register0(ChannelPromise promise) {

        try {

            if (!promise.setUncancellable() || !ensureOpen(promise)) {
                return;
            }

            // 真正的注册方法
            doRegister();

            // 修改注册状态
            registered = true;

            // 把成功状态赋值给promise, 这样它可以通知回调函数执行
            // 我们在之前注册的时候, 把bind也放在了回调函数中
            safeSetSuccess(promise);
            // 在这里给channel注册读事件
            beginRead();

        } catch (Exception e) {

            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public final void beginRead() {

        // 如果是服务端的channel, 这里仍然可能为false
        // 那么真正注册读事件的时机，就成了绑定端口号成功之后
        if (!isActive()) {
            return;
        }

        try {
            doBeginRead();
        } catch (final Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public final void bind(final SocketAddress localAddress, final ChannelPromise promise) {

        try {
            doBind(localAddress);;
            safeSetSuccess(promise);
        } catch (Exception e) {
            log.error(">>> bind报错", e);
        }
    }


    protected final void safeSetSuccess(ChannelPromise promise) {

        if (!promise.trySuccess()) {
            log.warn("Failed to mark a promise as success because it is done already: " + promise);
        }
    }

    protected void doRegister() throws Exception {

    }

    protected abstract void doBeginRead() throws Exception;


    protected abstract void doBind(SocketAddress localAddress) throws Exception;


    protected final boolean ensureOpen(ChannelPromise promise) {

        if (isOpen()) {
            return true;
        }

        safeSetFailure(promise, newClosedChannelException(initialCloseCause));
        return false;
    }

    private ClosedChannelException newClosedChannelException(Throwable cause) {

        ClosedChannelException exception = new ClosedChannelException();
        if (cause != null) {
            exception.initCause(cause);
        }
        return exception;
    }

    protected final void safeSetFailure(ChannelPromise promise, Throwable cause) {

        if (!promise.tryFailure(cause)) {
            throw new RuntimeException(cause);
        }
    }




    static final class CloseFuture extends DefaultChannelPromise {

        CloseFuture(AbstractChannel ch) {
            super(ch);
        }

        @Override
        public ChannelPromise setSuccess() {

            throw new IllegalStateException();
        }

        @Override
        public ChannelPromise setFailure(Throwable cause) {

            throw new IllegalStateException();
        }

        @Override
        public boolean trySuccess() {

            throw new IllegalStateException();
        }

        @Override
        public boolean tryFailure(Throwable cause) {
            throw new IllegalStateException();
        }

        boolean setClosed() {
            return super.trySuccess();
        }
    }

}
