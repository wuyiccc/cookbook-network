package com.wuyiccc.cookbook.network.mydemo.mydemo.demo05;

import java.nio.channels.spi.SelectorProvider;

/**
 * @author wuyiccc
 * @date 2025/1/14 00:22
 */
public class SelectorProviderTest {

    public static void main(String[] args) {

        SelectorProvider provider = SelectorProvider.provider();
        System.out.println(provider);
    }
}
