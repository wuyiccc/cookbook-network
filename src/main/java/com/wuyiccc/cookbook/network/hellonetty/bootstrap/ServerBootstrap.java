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
 * @author wuyiccc
 * @date 2024/11/22 22:53
 */
@Slf4j
public class ServerBootstrap extends AbstractBootstrap<ServerBootstrap, Channel> {


    private final Map<ChannelOption<?>, Object> childOptions = new LinkedHashMap<ChannelOption<?>, Object>();

    private final Map<AttributeKey<?>, Object> childAttrs = new LinkedHashMap<AttributeKey<?>, Object>();


    private final ServerBootstrapConfig config = new ServerBootstrapConfig(this);


    /**
     * BossGroup存放在抽象类中, 这里存放的是workGroup
     */
    private EventLoopGroup childGroup;


    public ServerBootstrap() {

    }

    private ServerBootstrap(ServerBootstrap bootstrap) {

        super(bootstrap);
        childGroup = bootstrap.childGroup;
        synchronized (bootstrap.childOptions) {
            childOptions.putAll(bootstrap.childOptions);
        }
        synchronized (bootstrap.childAttrs) {
            childAttrs.putAll(bootstrap.childAttrs);
        }
    }

    @Override
    public ServerBootstrap group(EventLoopGroup group) {
        return group(group, group);
    }

    public ServerBootstrap group(EventLoopGroup parentGroup, EventLoopGroup childGroup) {
        super.group(parentGroup);
        ObjectUtil.checkNotNull(childGroup, "childGroup");
        if (this.childGroup != null) {
            throw new IllegalStateException("childGroup set already");
        }
        this.childGroup = childGroup;
        return this;
    }

    public <T> ServerBootstrap childOption(ChannelOption<T> childOption, T value) {
        ObjectUtil.checkNotNull(childOption, "childOption");
        if (value == null) {
            synchronized (childOptions) {
                childOptions.remove(childOption);
            }
        } else {
            synchronized (childOptions) {
                childOptions.put(childOption, value);
            }
        }
        return this;
    }

    public <T> ServerBootstrap childAttr(AttributeKey<T> childKey, T value) {
        ObjectUtil.checkNotNull(childKey, "childKey");
        if (value == null) {
            childAttrs.remove(childKey);
        } else {
            childAttrs.put(childKey, value);
        }
        return this;
    }

    @Override
    void init(Channel channel) throws Exception {
        //得到所有存储在map中的用户设定的channel的参数
        final Map<ChannelOption<?>, Object> options = options0();
        synchronized (options) {
            //把初始化时用户配置的参数全都放到channel的config类中，因为没有引入netty源码的打印日志模块，
            //所以就把该方法修改了，去掉了日志参数
            setChannelOptions(channel, options);
        }
        final Map<AttributeKey<?>, Object> attrs = attrs0();
        synchronized (attrs) {
            for (Map.Entry<AttributeKey<?>, Object> e: attrs.entrySet()) {
                @SuppressWarnings("unchecked")
                AttributeKey<Object> key = (AttributeKey<Object>) e.getKey();
                channel.attr(key).set(e.getValue());
            }
        }
    }

    private static Map.Entry<AttributeKey<?>, Object>[] newAttrArray(int size) {
        return new Map.Entry[size];
    }

    private static Map.Entry<ChannelOption<?>, Object>[] newOptionArray(int size) {
        return new Map.Entry[size];
    }

    @Override
    public ServerBootstrap validate() {
        super.validate();
        //还没有引入channelHandler，先把这一段注释掉
//        if (childHandler == null) {
//            throw new IllegalStateException("childHandler not set");
//        }
        if (childGroup == null) {
            log.warn("childGroup is not set. Using parentGroup instead.");
            childGroup = config.group();
        }
        return this;
    }

    @Deprecated
    public EventLoopGroup childGroup() {
        return childGroup;
    }

    @Override
    public final ServerBootstrapConfig config() {
        return config;
    }

}