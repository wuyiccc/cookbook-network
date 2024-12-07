package com.wuyiccc.cookbook.network.hellonetty.bootstrap;


import com.wuyiccc.cookbook.network.hellonetty.channel.*;
import com.wuyiccc.cookbook.network.hellonetty.util.AttributeKey;
import com.wuyiccc.cookbook.network.hellonetty.util.concurrent.EventExecutor;
import com.wuyiccc.cookbook.network.hellonetty.util.internal.ObjectUtil;
import com.wuyiccc.cookbook.network.hellonetty.util.internal.SocketUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 这节课将会把bootstrao和serverbootstrap基本定型，但不会把源码中的所有方法都引入，剩下的一部分，在讲到ChannelPipeline
 * 的时候补充完整
 */
@Slf4j
public abstract class AbstractBootstrap<B extends AbstractBootstrap<B, C>, C extends Channel> {


    /**
     * bossgroup会赋值给这个group,当你创建的是NioSocketChannel的时候，workgroup就会赋值给该属性
     */
    volatile EventLoopGroup group;

    private volatile ChannelFactory<? extends C> channelFactory;

    private volatile SocketAddress localAddress;

    /**
     * 用户设定的NioServerSocketChannel的参数会暂时存放在这个map中，
     * channel初始化的时候，这里面的数据才会存放到channel的配置类中
     * 当然，当你创建的是NioSocketChannel的时候，这里存储的就是与NioSocketChannel有关的参数
     */
    private final Map<ChannelOption<?>, Object> options = new LinkedHashMap<ChannelOption<?>, Object>();

    /**
     * 在NioServerSocketChannel中传递额数据会暂时存放到这个map中，
     * 初始化channel才会把这个map中的数据存放到channel中，
     * 当你创建的是NioSocketChannel的时候，这里存储的就是与NioSocketChannel有关的参数
     */
    private final Map<AttributeKey<?>, Object> attrs = new LinkedHashMap<AttributeKey<?>, Object>();


    AbstractBootstrap() {

    }

    AbstractBootstrap(AbstractBootstrap<B, C> bootstrap) {

        group = bootstrap.group;
        channelFactory = bootstrap.channelFactory;
        localAddress = bootstrap.localAddress;
        synchronized (bootstrap.options) {
            options.putAll(bootstrap.options);
        }
        synchronized (bootstrap.attrs) {
            attrs.putAll(bootstrap.attrs);
        }
    }

    public B group(EventLoopGroup group) {

        ObjectUtil.checkNotNull(group, "group");
        if (this.group != null) {
            throw new IllegalStateException("group set already");
        }
        this.group = group;
        return self();
    }


    private B self() {
        return (B) this;
    }

    /**
     * 有了这个方法就可以把bootstrao和serverbootstrap中的同名方法删除了
     */
    public B channel(Class<? extends C> channelClass) {

        return channelFactory(new ReflectiveChannelFactory<C>(
                ObjectUtil.checkNotNull(channelClass, "channelClass")
        ));
    }

    public B channelFactory(ChannelFactory<? extends C> channelFactory) {

        ObjectUtil.checkNotNull(channelFactory, "channelFactory");

        if (this.channelFactory != null) {
            throw new IllegalStateException("channelFactory set already");
        }

        this.channelFactory = channelFactory;
        return self();
    }

    public B localAddress(SocketAddress localAddress) {

        this.localAddress = localAddress;
        return self();
    }

    public B localAddress(int inetPort) {

        return localAddress(new InetSocketAddress(inetPort));
    }


    public B localAddress(String inetHost, int inetPort) {
        return localAddress(SocketUtils.socketAddress(inetHost, inetPort));
    }


    public B localAddress(InetAddress inetHost, int inetPort) {

        return localAddress(new InetSocketAddress(inetHost, inetPort));
    }

    /**
     * 把用户定义的channel参数存入linkmap中，下面的方法同理
     */
    public <T> B option(ChannelOption<T> option, T value) {

        ObjectUtil.checkNotNull(option, "option");
        if (value == null) {
            synchronized (options) {
                options.remove(option);
            }
        } else {
            synchronized (options) {
                options.put(option, value);
            }
        }
        return self();
    }


    public <T> B attr(AttributeKey<T> key, T value) {

        ObjectUtil.checkNotNull(key, "key");
        if (value == null) {
            synchronized (attrs) {
                attrs.remove(key);
            }
        } else {
            synchronized (attrs) {
                attrs.put(key, value);
            }
        }
        return self();
    }

    public B validate() {

        if (group == null) {
            throw new IllegalStateException("group not set");
        }
        if (channelFactory == null) {
            throw new IllegalStateException("channel or channelFactory not set");
        }
        return self();
    }

    /**
     * 将channel注册到单线程执行器上的方法
     */
    public ChannelFuture register() {
        validate();
        return initAndRegister();
    }

    /**
     * bind方法本来就是定义在抽象类中的
     */
    public ChannelFuture bind() {
        validate();
        SocketAddress localAddress = this.localAddress;
        if (localAddress == null) {
            throw new IllegalStateException("localAddress not set");
        }
        return doBind(localAddress);
    }

    /**
     * 一般调用的是这个方法
     */
    public ChannelFuture bind(int inetPort) {
        return bind(new InetSocketAddress(inetPort));
    }


    public ChannelFuture bind(String inetHost, int inetPort) {
        return bind(SocketUtils.socketAddress(inetHost, inetPort));
    }


    public ChannelFuture bind(InetAddress inetHost, int inetPort) {
        return bind(new InetSocketAddress(inetHost, inetPort));
    }


    public ChannelFuture bind(SocketAddress localAddress) {
        validate();
        return doBind(ObjectUtil.checkNotNull(localAddress, "localAddress"));
    }

    private ChannelFuture doBind(final SocketAddress localAddress) {

        //服务端的channel在这里初始化，然后注册到单线程执行器的selector上
        final ChannelFuture regFuture = initAndRegister();
        //得到服务端的channel
        final Channel channel = regFuture.channel();
        if (regFuture.cause() != null) {
            return regFuture;
        }
        //要判断future有没有完成
        if (regFuture.isDone()) {
            //完成的情况下，直接开始执行绑定端口号的操作,首先创建一个future
            ChannelPromise promise = new DefaultChannelPromise(channel);
            //执行绑定方法
            doBind0(regFuture, channel, localAddress, promise);
            return promise;
        } else {
            //走到这里，说明上面的initAndRegister方法中，服务端的channel还没有完全注册到单线程执行器的selector上
            //此时可以直接则向regFuture添加回调函数，这里有个专门的静态内部类，用来协助判断服务端channel是否注册成功
            //该回调函数会在regFuture完成的状态下被调用，在回调函数中进行服务端的绑定，回顾一下第四课就明白了。
            final PendingRegistrationPromise promise = new PendingRegistrationPromise(channel);
            regFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    Throwable cause = future.cause();
                    if (cause != null) {
                        promise.setFailure(cause);
                    } else {
                        //走到这里说明服务端channel在注册过程中没有发生异常，已经注册成功，可以开始绑定端口号了
                        promise.registered();
                        doBind0(regFuture, channel, localAddress, promise);
                    }
                }
            });
            return promise;
        }
    }


    final ChannelFuture initAndRegister() {
        Channel channel = null;
        try {
            //在这里初始化服务端channel，反射创建对象调用的无参构造器，
            //可以去NioServerSocketChannel类中看看无参构造器中做了什么
            channel = channelFactory.newChannel();
            //初始化channel
            init(channel);
        } catch (Throwable t) {
            if (channel != null) {
                //出现异常则强制关闭channel
                channel.unsafe().closeForcibly();
                //返回一个赋值为失败的future
                return new DefaultChannelPromise(channel, channel.eventLoop()).setFailure(t);
            }
        }
        //在这里把channel注册到boss线程组的执行器上
        ChannelFuture regFuture = config().group().register(channel);
        if (regFuture.cause() != null) {
            //出现异常，但是注册成功了，则直接关闭channel，该方法还未实现，等我们课程到了末尾，讲到优雅停机和释放资源时，会讲解close方法
            if (channel.isRegistered()) {
                channel.close();
            } else {
                channel.unsafe().closeForcibly();
            }
        }
        return regFuture;
    }

    /**
     * 初始化channel的方法，这里定义为抽象的，意味着客户端channel和服务端channel实现的方法各不相同
     */
    abstract void init(Channel channel) throws Exception;


    /**
     * 真正的绑定服务端channel到端口号的方法
     */
    private static void doBind0(final ChannelFuture regFuture, final Channel channel,
                                final SocketAddress localAddress, final ChannelPromise promise) {
        //仍然是异步执行，其实只要记住这个异步执行就可以，剩下的具体逻辑，点进方法一步步debug就会很清楚了。真正干活的方法虽然会有很长的
        //调用链路，但是再长也长不过spring的链路，所以，这个很简单的啦，理解了类就够，看源码就会简单太多了。
        channel.eventLoop().execute(new Runnable() {
            @Override
            public void run() {
                //在这里仍要判断一次服务端的channel是否注册成功
                if (regFuture.isSuccess()) {
                    //注册成功之后开始绑定
                    channel.bind(localAddress, promise).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                } else {
                    //走到这里说明没有注册成功，把异常赋值给promise
                    promise.setFailure(regFuture.cause());
                }
            }
        });
    }


    public final EventLoopGroup group() {
        return group;
    }

    public abstract AbstractBootstrapConfig<B, C> config();

    final Map<ChannelOption<?>, Object> options0() {
        return options;
    }

    final Map<AttributeKey<?>, Object> attrs0() {
        return attrs;
    }

    final SocketAddress localAddress() {
        return localAddress;
    }

    final ChannelFactory<? extends C> channelFactory() {
        return channelFactory;
    }


    static void setChannelOptions(Channel channel, Map<ChannelOption<?>, Object> options) {
        for (Map.Entry<ChannelOption<?>, Object> e : options.entrySet()) {
            setChannelOption(channel, e.getKey(), e.getValue());
        }
    }

    static void setChannelOptions(
            Channel channel, Map.Entry<ChannelOption<?>, Object>[] options) {
        for (Map.Entry<ChannelOption<?>, Object> e : options) {
            setChannelOption(channel, e.getKey(), e.getValue());
        }
    }

    private static void setChannelOption(Channel channel, ChannelOption<?> option, Object value) {
        try {
            if (!channel.config().setOption((ChannelOption<Object>) option, value)) {
                log.warn("Unknown channel option '{}' for channel '{}'", option, channel);
            }
        } catch (Throwable t) {
            log.warn("Failed to set channel option '{}' with value '{}' for channel '{}'", option, value, channel, t);
        }
    }



    static final class PendingRegistrationPromise extends DefaultChannelPromise {

        private volatile boolean registered;

        PendingRegistrationPromise(Channel channel) {
            super(channel);
        }

        //该方法是该静态类独有的，该方法被调用的时候，registered赋值为true
        void registered() {
            registered = true;
        }


        @Override
        protected EventExecutor executor() {
            return super.executor();
//            if (registered) {
//                return super.executor();
//            }
//            这里返回一个全局的执行器，但我们没必要引入
//            return GlobalEventExecutor.INSTANCE;
        }
    }


}
