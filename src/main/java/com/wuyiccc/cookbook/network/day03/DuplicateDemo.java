package com.wuyiccc.cookbook.network.day03;

import java.nio.ByteBuffer;

/**
 * @author wuyiccc
 * @date 2024/11/9 09:13
 */
public class DuplicateDemo {

    public static void main(String[] args) {



        easyDemo();

    }

    public static void easyDemo() {

        ByteBuffer byteBuffer = ByteBuffer.allocate(100);

        // 复制之后的dupBuffer的初始参数指标与原byteBuffer一致, 但是后面修改的时候互不影响
        // 底层共用一个字节数组
        ByteBuffer dupBuffer = byteBuffer.duplicate();

        byteBuffer.put((byte) 1);

        byte b = dupBuffer.get();
        System.out.println(b == 1);
    }



}
