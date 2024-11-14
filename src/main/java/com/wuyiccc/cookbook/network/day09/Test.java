package com.wuyiccc.cookbook.network.day09;

import com.wuyiccc.cookbook.network.day09.request.RpcRequest;
import com.wuyiccc.cookbook.network.day09.serialize.HessianSerialize;

import java.io.IOException;
import java.util.UUID;

/**
 * @author wuyiccc
 * @date 2024/11/14 19:51
 */
public class Test {

    public static void main(String[] args) throws IOException {

        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setRequestId(UUID.randomUUID().toString());
        rpcRequest.setServiceInterfaceClass("testInterfaceClassName");

        byte[] bytes = HessianSerialize.serialize(rpcRequest);

        System.out.println(bytes.length);

        RpcRequest newRpcRequest = HessianSerialize.deserialize(bytes, RpcRequest.class);
        System.out.println(newRpcRequest.toString());
    }
}
