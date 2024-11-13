package com.wuyiccc.cookbook.network.day07;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;

/**
 * @author wuyiccc
 * @date 2024/11/13 07:25
 */
public class WrapByteBufTest {

    public static void main(String[] args) {

        String message = "hello";

        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);

        ByteBuf byteBufWrap = Unpooled.wrappedBuffer(bytes);

        while (byteBufWrap.isReadable()) {
            System.out.println("byteBufWrap的值: " + (char) byteBufWrap.readByte());
        }
    }
}
