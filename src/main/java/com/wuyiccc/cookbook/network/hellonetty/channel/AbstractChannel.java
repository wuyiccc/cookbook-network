package com.wuyiccc.cookbook.network.hellonetty.channel;

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

    private final Unsafe unsafe;


    private final ChannelId id;


    private final CloseFuture closeFuture = new CloseFuture(this);

    private volatile SocketAddress localAddress;

    private volatile SocketAddress remoteAddress;

    private Throwable initialCloseCause;

    private volatile EventLoop eventLoop;


    private volatile boolean registered;


    protected AbstractChannel(Channel parent) {
        this.parent = parent;
        unsafe = newUnsafe();
        id = newId();
    }

    protected AbstractChannel(Channel parent, ChannelId id) {

        this.parent = parent;
        this.id = id;
        unsafe = newUnsafe();
    }

    @Override
    public final ChannelId id() {

        return id;
    }


    protected ChannelId newId() {

        return DefaultChannelId.newInstance();
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
    public SocketAddress localAddress() {

        SocketAddress localAddress = this.localAddress;
        if (localAddress == null) {
            try {
                this.localAddress = localAddress = unsafe().localAddress();
            } catch (Error e) {
                throw e;
            } catch (Throwable t) {
                return null;
            }
        }
        return localAddress;
    }

    @Override
    public SocketAddress remoteAddress() {

        SocketAddress remoteAddress = this.remoteAddress;
        if (remoteAddress == null) {
            try {
                this.remoteAddress = remoteAddress = unsafe().remoteAddress();
            } catch (Error e) {
                throw e;
            } catch (Throwable t) {
                return null;
            }
        }
        return remoteAddress;
    }

    @Override
    public boolean isRegistered() {

        return registered;
    }

    protected abstract boolean isCompatible(EventLoop loop);


    @Override
    public ChannelFuture bind(SocketAddress localAddress) {
        return null;
    }


    @Override
    public ChannelFuture connect(SocketAddress remoteAddress) {
        return null;
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {

        return null;
    }

    @Override
    public ChannelFuture disconnect() {

        return null;
    }


    @Override
    public ChannelFuture close() {

        return null;
    }


    @Override
    public ChannelFuture deregister() {
        return null;
    }

    @Override
    public Channel flush() {
        return null;
    }

    @Override
    public ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) {
        unsafe.bind(localAddress, promise);
        return promise;
    }


    @Override
    public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
        return null;
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
        unsafe.connect(remoteAddress, localAddress, promise);
        return promise;
    }

    @Override
    public ChannelFuture disconnect(ChannelPromise promise) {
        return null;
    }

    @Override
    public ChannelFuture close(ChannelPromise promise) {
        return null;
    }

    @Override
    public ChannelFuture deregister(ChannelPromise promise) {
        return null;
    }

    @Override
    public Channel read() {

        unsafe.beginRead();
        return this;
    }

    @Override
    public ChannelFuture write(Object msg) {
        return null;
    }

    @Override
    public ChannelFuture write(Object msg, ChannelPromise promise) {
        return null;
    }

    @Override
    public ChannelFuture writeAndFlush(Object msg) {
        DefaultChannelPromise promise = new DefaultChannelPromise(this);
        unsafe.write(msg, promise);
        return promise;
    }

    @Override
    public ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
        return null;
    }

    @Override
    public ChannelPromise newPromise() {
        return null;
    }

    @Override
    public ChannelFuture newSucceededFuture() {
        return null;
    }

    @Override
    public ChannelFuture newFailedFuture(Throwable cause) {
        return null;
    }

    @Override
    public ChannelFuture closeFuture() {
        return closeFuture;
    }

    @Override
    public Unsafe unsafe() {
        return unsafe;
    }

    protected abstract AbstractUnsafe newUnsafe();

    /**
     * Unsafe的抽象内部类，看到上面一下子多了那么多方法，而且还有很多是没实现的，一定会感到很懵逼
     * 但是，请记住这句话，在抽象类中，很多方法尽管实现了方法体，内在逻辑却并不复杂。因为这些方法几乎都是为子类服务的，
     * 换句话说，就是定义了一些模版方法而已，真正实现的方法在子类之中。就比调用了AbstractChannel抽象类的bind方法，
     * 在该方法内部，又会调用unsafe抽象内部类的bind方法，而该内部类的bind方法又会调用AbstractChannel类中的另一个抽象方法
     * doBind，虽然NioServerChannel中也有该方法的实现，但该方法在子类NioServerSocketChannel中才是真正实现。
     * 我想，这时候有的朋友可能又会困惑作者为什么这样设计类的继承结构。也许有的朋友已经清楚了，但我在这里再啰嗦一句，
     * 首先，channel分为客户端和服务端，因为抽象出了公共的接口和父抽象类，两种channel不得不实现相同的方法，
     * 那么不同的channel实现的相同方法的逻辑应该不同，所以dobind设计为抽象方法是很合理的。因为你不能让NiosocketChannel客户端channel
     * 向服务端channel那样去绑定端口，虽然要做也确实可以这么做。。
     * 按照我的这种思路，大家可以思考思考，为什么继承了同样的接口，有的方法可以出现在这个抽象类中，有的方法可以出现在那个抽象类中，为什么有的方法要设计成抽象的
     */
    protected abstract class AbstractUnsafe implements Unsafe {

        private void assertEventLoop() {
            assert !registered || eventLoop.inEventLoop(Thread.currentThread());
        }

        @Override
        public final SocketAddress localAddress() {
            return localAddress0();
        }

        @Override
        public final SocketAddress remoteAddress() {
            return remoteAddress0();
        }

        /**
         * 该方法终于回到了它本来的位置
         */
        @Override
        public final void register(EventLoop eventLoop, final ChannelPromise promise) {
            if (eventLoop == null) {
                throw new NullPointerException("eventLoop");
            }
            //检查channel是否注册过，注册过就手动设置promise失败
            if (isRegistered()) {
                promise.setFailure(new IllegalStateException("registered to an event loop already"));
                return;
            }
            //判断当前使用的执行器是否为NioEventLoop，如果不是手动设置失败
            if (!isCompatible(eventLoop)) {
                promise.setFailure(new IllegalStateException("incompatible event loop type: " + eventLoop.getClass().getName()));
                return;
            }
            //稍微学过netty的人都知道，一个channel绑定一个单线程执行器。终于在这里，我们看到channel绑定了单线程执行器
            //接着channel，不管是客户端还是服务端的，会把自己注册到绑定的单线程执行器中的selector上
            AbstractChannel.this.eventLoop = eventLoop;
            //又看到这个方法了，又一次说明在netty中，channel注册，绑定，连接等等都是异步的，由单线程执行器来执行
            if (eventLoop.inEventLoop(Thread.currentThread())) {
                register0(promise);
            } else {
                try {
                    //如果调用该放的线程不是netty的线程，就封装成任务由线程执行器来执行
                    eventLoop.execute(new Runnable() {
                        @Override
                        public void run() {
                            register0(promise);
                        }
                    });
                } catch (Throwable t) {
                    log.error(">>> register异常", t);
                    //该方法先不做实现，等引入unsafe之后会实现
                    //closeForcibly();
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
                //真正的注册方法
                doRegister();
                //修改注册状态
                registered = true;
                //把成功状态赋值给promise，这样它可以通知回调函数执行
                //我们在之前注册时候，把bind也放在了回调函数中
                safeSetSuccess(promise);
                //在这里给channel注册读事件
                beginRead();
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        @Override
        public final void bind(final SocketAddress localAddress, final ChannelPromise promise) {

            try {
                doBind(localAddress);
                safeSetSuccess(promise);
            } catch (Exception e) {
                log.error(">>> bind异常", e);
            }
        }

        /**
         * 暂时不做实现，接下来的一些方法都不做实现，等之后讲到了再实现
         */
        @Override
        public final void disconnect(final ChannelPromise promise) {
        }

        @Override
        public final void close(final ChannelPromise promise) {
        }

        /**
         * 强制关闭channel
         */
        @Override
        public final void closeForcibly() {
            assertEventLoop();

            try {
                doClose();
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        @Override
        public final void deregister(final ChannelPromise promise) {
        }

        @Override
        public final void beginRead() {

            assertEventLoop();
            //如果是服务端的channel，这里仍然可能为false
            //那么真正注册读事件的时机，就成了绑定端口号成功之后
            if (!isActive()) {
                return;
            }
            try {
                doBeginRead();
            } catch (final Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        /**
         * 这个方法在上一节课没有实现，在这一节课，我们也不会真正的实现它，但是可以在这里稍微加点东西，
         * 讲解一下发送消息的逻辑和步骤 ByteBuf讲完了之后，我们再重写该方法。在这里，该方法只是把数据发送到了一个netty自定义的缓冲区中，还没有放入socket的缓冲区
         * 真正的write方法在子类NioSocketChannel中实现，在那个方法中，数据才被真正放进socket的缓冲区中
         * 根据之前的老套路，仍需要在AbstractChannel抽象类中定义一个抽象方法，让子类去实现
         */
        @Override
        public final void write(Object msg, ChannelPromise promise) {
            try {
                doWrite(msg);
                //如果有监听器，这里可以通知监听器执行回调方法
                promise.trySuccess();
            } catch (Exception e) {
                log.error(">>> write异常", e);
            }
        }

        /**
         * 在源码中，如果调用的是writeAndFlush方法发送数据，在write方法执行后，会紧接着执行flush方法 这个我们也会在后面讲到
         */
        @Override
        public final void flush() {
        }

        /**
         * 确保channel是打开的
         */
        protected final boolean ensureOpen(ChannelPromise promise) {
            if (isOpen()) {
                return true;
            }
            safeSetFailure(promise, newClosedChannelException(initialCloseCause));
            return false;
        }

        protected final void safeSetSuccess(ChannelPromise promise) {

            if (!promise.trySuccess()) {

                log.info("Failed to mark a promise as success because it is done already: " + promise);
            }
        }

        protected final void safeSetFailure(ChannelPromise promise, Throwable cause) {

            if (!promise.tryFailure(cause)) {

                throw new RuntimeException(cause);
            }
        }
    }

    /**
     * 该方法的位置正确，但是参数和源码有差异，源码的参数是netty自己定义的ChannelOutboundBuffer，是出站的数据缓冲区 现在，我们先用最简单的实现，之后再重写
     */
    protected abstract void doWrite(Object msg) throws Exception;

    protected abstract SocketAddress localAddress0();

    protected abstract SocketAddress remoteAddress0();

    protected void doRegister() throws Exception {

    }

    protected abstract void doBind(SocketAddress localAddress) throws Exception;

    protected abstract void doBeginRead() throws Exception;

    protected abstract void doClose() throws Exception;

    private ClosedChannelException newClosedChannelException(Throwable cause) {

        ClosedChannelException exception = new ClosedChannelException();
        if (cause != null) {
            exception.initCause(cause);
        }
        return exception;
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
