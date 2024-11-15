package com.wuyiccc.cookbook.network.day09.client;

import com.wuyiccc.cookbook.network.day09.protocol.response.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author wuyiccc
 * @date 2024/11/15 22:53
 */
public class RpcReadTimeoutHandler extends ChannelInboundHandlerAdapter {


    private long timeout;

    public RpcReadTimeoutHandler(long timeout) {
        this.timeout = timeout;
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        RpcResponse response = (RpcResponse) msg;

        long requestTime = RpcRequestTimeHolder.get(response.getRequestId());
        long now = System.currentTimeMillis();

        if (now - requestTime >= timeout) {
            response.setTimeout(true);
            System.out.println("服务端响应超时");
        } else {
            response.setTimeout(false);
        }

        RpcRequestTimeHolder.remove(response.getRequestId());
        super.channelRead(ctx, msg);
    }
}
