package com.wuyiccc.cookbook.network.day02;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @author wuyiccc
 * @date 2024/11/5 22:13
 */
public class ByteBuffTest {
    public static void main(String[] args) {


        ByteBuffer byteBuffer = ByteBuffer.allocate(10);

        System.out.println("第一步, 查看各个参数的数值");
        System.out.println(byteBuffer.position());
        System.out.println(byteBuffer.limit());
        System.out.println(byteBuffer.capacity());


        System.out.println("第二步, 放入四个元素");
        byteBuffer.put("a".getBytes(StandardCharsets.UTF_8));
        byteBuffer.put("b".getBytes(StandardCharsets.UTF_8));
        byteBuffer.put("c".getBytes(StandardCharsets.UTF_8));
        byteBuffer.put("d".getBytes(StandardCharsets.UTF_8));

        System.out.println("第三步, 查看各个参数的值");
        System.out.println(byteBuffer.position());
        System.out.println(byteBuffer.limit());
        System.out.println(byteBuffer.capacity());

        System.out.println("第四步, 从写模式改为读模式");
        byteBuffer.flip();

        System.out.println("第五步, 读出第一个字母");
        System.out.println((char) byteBuffer.get());
        System.out.println((char) byteBuffer.get());
        System.out.println((char) byteBuffer.get());
        System.out.println((char) byteBuffer.get());
        System.out.println((char) byteBuffer.get());
        System.out.println();



    }
}