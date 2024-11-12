package com.wuyiccc.cookbook.network.day07;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * @author wuyiccc
 * @date 2024/11/13 00:19
 */
public class ByteBufTest {

    public static void main(String[] args) {

        ByteBuf byteBuf = init();


        System.out.println("测试一");
        getByteBuf(byteBuf);

        System.out.println("测试二");
        readByteBuf(byteBuf);
    }

    private static ByteBuf init() {

        // 指定初始容量为9, 最大容量为100的缓冲区
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(9, 100);

        System.out.println("Buffer的初始化: " + buffer);

        buffer.writeBytes(new byte[] {1, 2, 3, 4});

        System.out.println("Buffer的写入操作: " + buffer);

        return buffer;
    }

    private static void getByteBuf(ByteBuf buffer) {

        // 读字节, 不改变指针
        for (int i = 0; i < buffer.readableBytes(); i++) {
            System.out.println("读" + i + "个字节: " + buffer.getByte(i));
        }
    }

    private static void readByteBuf(ByteBuf buffer) {

        // 读字节, 改变指针 readerIndex++
        while (buffer.isReadable()) {
            System.out.println("取第一个字节: " + buffer.readByte());
        }
    }

}
