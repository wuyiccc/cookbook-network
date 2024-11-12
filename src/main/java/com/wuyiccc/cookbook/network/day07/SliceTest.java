package com.wuyiccc.cookbook.network.day07;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * @author wuyiccc
 * @date 2024/11/13 07:13
 */
public class SliceTest {

    public static void main(String[] args) {

        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(4, 10);
        buffer.writeBytes(new byte[] {1, 2, 3, 4});

        // 获取可读部分的切片 [readerIndex, writeIndex)
        ByteBuf sliceA = buffer.slice();


        // 设置切片的起始位置和长度: length+index 不能>= writeIndex
        ByteBuf sliceB = buffer.slice(0, 2);

        System.out.println(sliceA.getByte(0));
        System.out.println(sliceB.getByte(0));



        sliceA.setByte(0, 9);


        System.out.println(sliceA.getByte(0));
        System.out.println(sliceB.getByte(0));

        // 共享底层数组, 但是指标参数互不影响
        ByteBuf duplicate = buffer.duplicate();
        System.out.println(duplicate.getByte(0));

        duplicate.writeByte(5);
        duplicate.writeByte(6);
        duplicate.writeByte(7);

        System.out.println(buffer.readByte());
        System.out.println(duplicate.readByte());
        System.out.println(buffer.readByte());
        System.out.println(duplicate.readByte());
    }
}
