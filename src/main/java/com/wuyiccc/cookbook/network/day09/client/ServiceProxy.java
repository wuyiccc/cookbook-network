package com.wuyiccc.cookbook.network.day09.client;

import com.wuyiccc.cookbook.network.day09.protocol.request.RpcRequest;
import com.wuyiccc.cookbook.network.day09.protocol.response.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * @author wuyiccc
 * @date 2024/11/15 23:22
 */
public class ServiceProxy {

    public static Object createProxy(ReferenceConfig referenceConfig) {

        return Proxy.newProxyInstance(ServiceProxy.class.getClassLoader()
                , new Class[]{referenceConfig.getServerInterfaceClass()}
                , new ServiceProxyInvocationHandler(referenceConfig));
    }

    static class ServiceProxyInvocationHandler implements InvocationHandler {

        private ReferenceConfig referenceConfig;

        public ServiceProxyInvocationHandler(ReferenceConfig referenceConfig) {
            this.referenceConfig = referenceConfig;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            RpcClient rpcClient = new RpcClient(referenceConfig);
            rpcClient.connect();

            RpcRequest request = new RpcRequest();
            request.setRequestId(UUID.randomUUID().toString());
            request.setServiceInterfaceClass(referenceConfig.getServerInterfaceClass().getName());
            request.setMethodName(method.getName());
            request.setParameterTypes(method.getParameterTypes());
            request.setArgs(args);

            RpcResponse response = rpcClient.remoteCall(request);

            return response.getResult();
        }
    }
}
