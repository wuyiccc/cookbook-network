package com.wuyiccc.cookbook.network.day02;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @author wuyiccc
 * @date 2024/11/7 21:55
 */
public class demo03 {

    public static void main(String[] args) {


        byte[] data = "Some data".getBytes(StandardCharsets.UTF_8);
        // 基于已有的数组去构建byteBuffer, 而不是让ByteBuffer自己再去构建一个底层存储数组
        ByteBuffer byteBuffer = ByteBuffer.wrap(data);

        byte[] byteBufferArray = byteBuffer.array();


        // 两者的数组其实是同一个
        System.out.println(data == byteBufferArray);
    }
}
