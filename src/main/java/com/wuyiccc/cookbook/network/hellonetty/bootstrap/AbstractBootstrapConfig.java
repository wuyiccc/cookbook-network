package com.wuyiccc.cookbook.network.hellonetty.bootstrap;


import com.wuyiccc.cookbook.network.hellonetty.channel.Channel;
import com.wuyiccc.cookbook.network.hellonetty.channel.ChannelFactory;
import com.wuyiccc.cookbook.network.hellonetty.channel.EventLoopGroup;
import com.wuyiccc.cookbook.network.hellonetty.util.internal.ObjectUtil;
import com.wuyiccc.cookbook.network.hellonetty.util.internal.StringUtil;

import java.net.SocketAddress;



public abstract class AbstractBootstrapConfig<B extends AbstractBootstrap<B, C>, C extends Channel> {

    protected final B bootstrap;

    protected AbstractBootstrapConfig(B bootstrap) {
        this.bootstrap = ObjectUtil.checkNotNull(bootstrap, "bootstrap");
    }

    public final SocketAddress localAddress() {
        return bootstrap.localAddress();
    }


    public final ChannelFactory<? extends C> channelFactory() {
        return bootstrap.channelFactory();
    }



    public final EventLoopGroup group() {
        return bootstrap.group();
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder()
                .append(StringUtil.simpleClassName(this))
                .append('(');
        EventLoopGroup group = group();
        if (group != null) {
            buf.append("group: ")
                    .append(StringUtil.simpleClassName(group))
                    .append(", ");
        }
        ChannelFactory<? extends C> factory = channelFactory();
        if (factory != null) {
            buf.append("channelFactory: ")
                    .append(factory)
                    .append(", ");
        }
        SocketAddress localAddress = localAddress();
        if (localAddress != null) {
            buf.append("localAddress: ")
                    .append(localAddress)
                    .append(", ");
        }
        if (buf.charAt(buf.length() - 1) == '(') {
            buf.append(')');
        } else {
            buf.setCharAt(buf.length() - 2, ')');
            buf.setLength(buf.length() - 1);
        }
        return buf.toString();
    }
}
