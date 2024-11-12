package com.wuyiccc.cookbook.network.day07;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * @author wuyiccc
 * @date 2024/11/13 00:44
 */
public class RefByteBufTest {

    public static void main(String[] args) {

        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();
        System.out.println("初始化后引用计数器: " + byteBuf.refCnt());


        // 增加一次
        byteBuf.retain();
        System.out.println("调用retain()后引用计数器: " + byteBuf.refCnt());


        // 减少一次引用计数
        byteBuf.release();
        System.out.println("调用release()后引用计数器: " + byteBuf.refCnt());

        byteBuf.release();
        System.out.println("调用release()后引用计数器: " + byteBuf.refCnt());



        // 如果refCnf==0之后, 就不能在使用byteBuf了
        byteBuf.retain();
        System.out.println("调用retain()后引用计数器: " + byteBuf.refCnt());
    }
}
