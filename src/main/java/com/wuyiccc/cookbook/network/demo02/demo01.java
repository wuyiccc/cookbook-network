package com.wuyiccc.cookbook.network.demo02;

import sun.nio.ch.DirectBuffer;

import java.nio.Buffer;
import java.nio.ByteBuffer;

/**
 * @author wuyiccc
 * @date 2024/11/7 20:29
 */
public class demo01 {

    public static void main(String[] args) {

        ByteBuffer byteBuffer = ByteBuffer.allocate(100);

        // 获取byteBuffer position的位置
        int position = byteBuffer.position();
        System.out.println(position);
        // 修改byteBuffer position的位置
        byteBuffer.position(5);
        System.out.println(byteBuffer.position());


        // capacity, 缓冲区可以保存的元素的最大数目, 容量值在创建缓冲区的时候设置, 伺候不能改变
        int capacity = byteBuffer.capacity();
        System.out.println("容量大小: " + capacity);

        // limit限制, 缓冲区可以访问数据的末尾位置, 只要不改变, 就无法读/写超过这个位置的数据, 即使缓冲区有更大的容量也没有用
        int limit = byteBuffer.limit();
        System.out.println("读取限制limit: " + limit);

        // 修改限制为10
        byteBuffer.limit(10);
        limit = byteBuffer.limit();
        System.out.println("读取限制limit: " + limit);
        byteBuffer.limit(100);


        byteBuffer.mark();

        byteBuffer.position(80);
        int updatePosition = byteBuffer.position();
        System.out.println("修改后的位置: " + updatePosition);

        byteBuffer.reset();
        int resetPosition = byteBuffer.position();
        System.out.println("重置之后的位置: " + resetPosition);


        // 重置 position, limit, mark 标志位的值
        byteBuffer.clear();

        // 重置position与mark的值
        byteBuffer.rewind();

        // 修改limit为position的位置, position=0, mark=-1
        byteBuffer.flip();

        // 返回当前position距离limit还有多少个位置: limit - position
        byteBuffer.remaining();

        // position < limit的结果
        boolean flag = byteBuffer.hasRemaining();

    }
}
