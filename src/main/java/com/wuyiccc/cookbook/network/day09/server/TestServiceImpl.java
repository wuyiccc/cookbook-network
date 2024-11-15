package com.wuyiccc.cookbook.network.day09.server;

/**
 * @author wuyiccc
 * @date 2024/11/15 22:20
 */
public class TestServiceImpl implements TestService{
    @Override
    public String sayHello(String name) {

        return "netty server response: " + name;
    }
}
