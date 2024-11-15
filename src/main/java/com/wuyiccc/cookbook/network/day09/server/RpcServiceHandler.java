package com.wuyiccc.cookbook.network.day09.server;

import com.wuyiccc.cookbook.network.day09.protocol.request.RpcRequest;
import com.wuyiccc.cookbook.network.day09.protocol.response.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wuyiccc
 * @date 2024/11/15 22:04
 */
public class RpcServiceHandler extends ChannelInboundHandlerAdapter {

    private ConcurrentHashMap<String, ServiceConfig> serviceConfigMap = new ConcurrentHashMap<>();

    public RpcServiceHandler(List<ServiceConfig> serviceConfigList) {

        for (ServiceConfig serviceConfig : serviceConfigList) {

            Class<?> key = serviceConfig.getServiceInterfaceClass();
            serviceConfigMap.put(key.getName(), serviceConfig);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        // 1. 接受请求对象
        RpcRequest request = (RpcRequest) msg;

        System.out.println("netty rpc server 接受到请求数据: " + request);

        RpcResponse response = new RpcResponse();
        response.setRequestId(request.getRequestId());


        try {

            ServiceConfig serviceConfig = serviceConfigMap.get(request.getServiceInterfaceClass());
            Class clazz = serviceConfig.getServiceClass();
            Object instance = clazz.newInstance();

            Method method = clazz.getMethod(request.getMethodName(), request.getParameterTypes());
            Object result = method.invoke(instance, request.getArgs());

            // 将调用结果放回到响应中去
            response.setResult(result);
            response.setSuccess(RpcResponse.SUCCESS);
        } catch (Exception e) {
            response.setSuccess(RpcResponse.FAILURE);
            response.setException(e);
        }

        ctx.channel().writeAndFlush(response);
    }
}
