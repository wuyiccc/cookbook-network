package com.wuyiccc.cookbook.network.day09.client;

/**
 * @author wuyiccc
 * @date 2024/11/15 22:44
 */
public class RpcReadTimeoutException extends RuntimeException {

    public RpcReadTimeoutException(String message) {

        super(message);
    }
}
