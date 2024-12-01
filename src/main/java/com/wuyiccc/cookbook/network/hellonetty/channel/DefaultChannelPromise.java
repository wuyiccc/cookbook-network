package com.wuyiccc.cookbook.network.hellonetty.channel;

import com.wuyiccc.cookbook.network.hellonetty.util.concurrent.*;
import com.wuyiccc.cookbook.network.hellonetty.util.internal.ObjectUtil;

/**
 * @author wuyiccc
 * @date 2024/11/30 21:49
 */
public class DefaultChannelPromise extends DefaultPromise<Void> implements ChannelPromise {


    private final Channel channel;

    public DefaultChannelPromise(Channel channel) {
        this.channel = ObjectUtil.checkNotNull(channel, "channel");
    }


    public DefaultChannelPromise(Channel channel, EventExecutor executor) {
        super(executor);
        this.channel = ObjectUtil.checkNotNull(channel, "channel");
    }

    @Override
    protected EventExecutor executor() {

        EventExecutor e = super.executor();
        if (e == null) {
            return channel().eventLoop();
        } else {
            return e;
        }
    }

    @Override
    public Channel channel() {
        return channel;
    }

    @Override
    public ChannelPromise setSuccess() {
        return setSuccess(null);
    }

    @Override
    public ChannelPromise setSuccess(Void result) {

        super.setSuccess(result);
        return this;
    }

    @Override
    public boolean trySuccess() {

        return trySuccess(null);
    }


    @Override
    public ChannelPromise setFailure(Throwable cause) {

        super.setFailure(cause);
        return this;
    }

    @Override
    public ChannelPromise addListener(GenericFutureListener<? extends Future<? super Void>> listener) {

        super.addListener(listener);
        return this;
    }

    @Override
    public ChannelPromise addListeners(GenericFutureListener<? extends Future<? super Void>> ...listeners) {

        super.addListeners(listeners);
        return this;
    }

    @Override
    public ChannelPromise removeListener(GenericFutureListener<? extends Future<? super Void>> listener) {

        super.removeListener(listener);
        return this;
    }

    @Override
    public ChannelPromise removeListeners(GenericFutureListener<? extends Future<? super Void>> ...listeners) {

        super.removeListeners(listeners);
        return this;
    }

    @Override
    public ChannelPromise sync() throws InterruptedException {

        super.sync();
        return this;
    }

    @Override
    public ChannelPromise syncUninterruptibly() {

        super.syncUninterruptibly();
        return this;
    }

    @Override
    public ChannelPromise await() throws InterruptedException {
        super.await();
        return this;
    }

    @Override
    public ChannelPromise awaitUninterruptibly() {

        super.awaitUninterruptibly();
        return this;
    }

    @Override
    protected void checkDeadLock() {

        if (channel().isRegistered()) {
            super.checkDeadLock();
        }
    }

    @Override
    public ChannelPromise unvoid() {

        return this;
    }

    @Override
    public boolean isVoid() {

        return false;
    }


}
