package com.wuyiccc.cookbook.network.day09.client;

import com.wuyiccc.cookbook.network.day08.NettyClientHandler;
import com.wuyiccc.cookbook.network.day09.handler.coder.RpcDecoder;
import com.wuyiccc.cookbook.network.day09.handler.coder.RpcEncoder;
import com.wuyiccc.cookbook.network.day09.protocol.request.RpcRequest;
import com.wuyiccc.cookbook.network.day09.protocol.response.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

/**
 * @author wuyiccc
 * @date 2024/11/15 23:07
 */
public class RpcClient {

    private ReferenceConfig referenceConfig;

    private ChannelFuture channelFuture;


    public RpcClient(ReferenceConfig referenceConfig) {
        this.referenceConfig = referenceConfig;
    }

    public void connect() throws InterruptedException {

        EventLoopGroup workGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {

                        ch.pipeline().addLast(new RpcDecoder(RpcResponse.class));
                        ch.pipeline().addLast(new RpcEncoder(RpcRequest.class));

                        ch.pipeline().addLast(new RpcReadTimeoutHandler(ReferenceConfig.DEFAULT_TIMEOUT));
                        ch.pipeline().addLast(new RpcClientHandler());
                    }
                });

        channelFuture = bootstrap.connect(ReferenceConfig.DEFAULT_SERVER_HOST, ReferenceConfig.DEFAULT_SERVER_PORT).sync();

        channelFuture.channel().closeFuture().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {

                workGroup.shutdownGracefully();
            }
        });
    }

    public RpcResponse remoteCall(RpcRequest rpcRequest) throws Throwable {

        RpcRequestTimeHolder.put(rpcRequest.getRequestId(), System.currentTimeMillis());

        channelFuture.channel().writeAndFlush(rpcRequest).sync();

        RpcResponse response = RpcClientHandler.getResponse(rpcRequest.getRequestId());

        if (response.getSuccess()) {
            return response;
        }

        throw response.getException();
    }
}
