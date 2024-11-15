package com.wuyiccc.cookbook.network.day09.client;

import com.wuyiccc.cookbook.network.day09.protocol.response.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author wuyiccc
 * @date 2024/11/15 22:59
 */
public class RpcClientHandler extends ChannelInboundHandlerAdapter {

    private static final long GET_RPC_RESPONSE_SLEEP_INTERNAL = 5;

    private static ConcurrentHashMap<String, RpcResponse> responseMap = new ConcurrentHashMap<>();

    private static long timeout = 5000;

    public RpcClientHandler() {
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        RpcResponse response = (RpcResponse) msg;

        if (response.getTimeout()) {
            System.out.println("响应超时...");
        } else {
            responseMap.put(response.getRequestId(), response);
            System.out.println("接受到服务端响应消息");
        }
    }


    public static RpcResponse getResponse(String requestId) throws InterruptedException {

        long waitStartTime = System.currentTimeMillis();

        while (responseMap.get(requestId) == null) {

            long now = System.currentTimeMillis();

            if (now - waitStartTime >= timeout) {
                break;
            }

            TimeUnit.MILLISECONDS.sleep(GET_RPC_RESPONSE_SLEEP_INTERNAL);
        }

        RpcResponse response = responseMap.get(requestId);

        if (Objects.isNull(response)) {
            throw new RpcReadTimeoutException("服务端响应超时");
        }

        responseMap.remove(requestId);

        return response;
    }
}
