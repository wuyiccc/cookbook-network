package com.wuyiccc.cookbook.network.day03;

import java.nio.ByteBuffer;

/**
 * @author wuyiccc
 * @date 2024/11/9 08:53
 */
public class CompactDemo {

    public static void main(String[] args) {


        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 2);
        byteBuffer.put((byte) 3);
        byteBuffer.put((byte) 4);
        byteBuffer.put((byte) 5);

        byteBuffer.flip();
        byte b = byteBuffer.get();
        System.out.println(b);
        b = byteBuffer.get();
        System.out.println(b);


        // 压缩数据, 将前面已经读取完毕的数据进行清空, 未读取完成的数据移动到最前面
        byteBuffer.compact();
        byteBuffer.put((byte) 6);
        byteBuffer.put((byte) 7);

        // 调整为读模式
        byteBuffer.flip();

        // 循环读取剩余的数据
        while (byteBuffer.hasRemaining()) {
            byte data = byteBuffer.get();
            System.out.println(data);
        }
    }
}
