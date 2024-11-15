package com.wuyiccc.cookbook.network.day09.client;

import com.wuyiccc.cookbook.network.day09.server.TestService;

/**
 * @author wuyiccc
 * @date 2024/11/15 23:27
 */
public class RpcClientTest {

    public static void main(String[] args) {

        ReferenceConfig referenceConfig = new ReferenceConfig(TestService.class);

        TestService testService = (TestService) ServiceProxy.createProxy(referenceConfig);

        String result = testService.sayHello("sean");

        System.out.println("result: " + result);

    }
}
