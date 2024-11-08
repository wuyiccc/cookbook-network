package com.wuyiccc.cookbook.network.day02;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author wuyiccc
 * @date 2024/11/7 22:44
 */
public class demo05 {

    public static void main(String[] args) {


        // 默认模式下byteBuffer都以大端对齐的方式进行读写, 写入int的时候, int的高位值首先写入第一个byte中
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);

        // 十进制19转为二进制为 000000000 00000000 00000000 00010011
        // 默认采用大端对齐方式, 那么0023的高位应该放在byteBuffer的低位
        // 那么putByte的顺序应该是 put(0) -> put(0) -> put(0) -> put(19)
        byteBuffer.putInt(19);

        byte b = byteBuffer.get(3);
        System.out.println(b == 19);

        ByteOrder order = byteBuffer.order();
        System.out.println(order);

        // 手动修改为小端对齐
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);

    }
}
