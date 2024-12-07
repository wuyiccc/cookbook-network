package com.wuyiccc.cookbook.network.hellonetty.bootstrap;


import com.wuyiccc.cookbook.network.hellonetty.channel.Channel;
import com.wuyiccc.cookbook.network.hellonetty.channel.EventLoopGroup;
import com.wuyiccc.cookbook.network.hellonetty.util.internal.StringUtil;

public final class ServerBootstrapConfig extends AbstractBootstrapConfig<ServerBootstrap, Channel> {

    ServerBootstrapConfig(ServerBootstrap bootstrap) {
        super(bootstrap);
    }

    public EventLoopGroup childGroup() {
        return bootstrap.childGroup();
    }

    @Override
    public String toString() {

        StringBuilder buf = new StringBuilder(super.toString());
        buf.setLength(buf.length() - 1);
        buf.append(", ");
        EventLoopGroup childGroup = childGroup();
        if (childGroup != null) {
            buf.append("childGroup: ");
            buf.append(StringUtil.simpleClassName(childGroup));
            buf.append(", ");
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
