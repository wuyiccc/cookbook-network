package com.wuyiccc.cookbook.network.day07;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;

/**
 * @author wuyiccc
 * @date 2024/11/13 08:27
 */
public class CompositeByteBufTest {

    public static void main(String[] args) {


        ByteBuf byteBufA = Unpooled.buffer();
        byteBufA.writeBytes("a".getBytes());

        for (int i = 0; i < byteBufA.readableBytes(); i++) {
            System.out.println("byteBufA 的值：" + (char) byteBufA.getByte(i));
        }

        ByteBuf byteBufB = Unpooled.buffer();
        byteBufB.writeBytes("b".getBytes());
        for (int i = 0; i < byteBufB.readableBytes(); i++) {
            System.out.println("byteBufB 的值：" + (char) byteBufB.getByte(i));
        }

        // 创建 compositeByteBuf: 底层共享数组结构, 如果compositeByteBuf结构发生改变，其他组成的ByteBuf的数据也会发生改变
        CompositeByteBuf compositeByteBuf = Unpooled.compositeBuffer();
        // 拼接两个 byteBuf
        compositeByteBuf.addComponents(true, byteBufA, byteBufB);

        byteBufB.setByte(0, 'c');

        printBuf(compositeByteBuf);


    }

    static void printBuf(ByteBuf byteBuf) {

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < byteBuf.writerIndex(); i++) {
            stringBuilder.append((char) byteBuf.getByte(i));
        }

        System.out.println("compositeByteBuf的值为: " + stringBuilder);
    }
}
