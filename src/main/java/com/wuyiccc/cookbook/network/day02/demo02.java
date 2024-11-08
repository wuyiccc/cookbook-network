package com.wuyiccc.cookbook.network.day02;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

/**
 * @author wuyiccc
 * @date 2024/11/7 21:18
 */
public class demo02 {

    public static void main(String[] args) {

        // 用allocate创建的缓冲区基于java的数组实现
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
        System.out.println(byteBuffer.capacity());

        // 获取buffer的核心存储结构数组, 修改后的数组会反映到缓冲区中
        byte[] array = byteBuffer.array();
        System.out.println(array[0]);
        // arrayOffset获取当前byteBuffer第一个元素相对于底层数组的偏移量, 从0初始化byteBuffer的时候, offset直接就是底层数组的第一个元素
        // 即为0
        int res = byteBuffer.arrayOffset();
        System.out.println(res);

        IntBuffer intBuffer = IntBuffer.allocate(100);
        System.out.println(intBuffer.capacity());


        // 直接分配内存
        ByteBuffer directByteBuffer = ByteBuffer.allocateDirect(100);

        // 抛出 UnsupportedOperationException 异常, 直接内存显然不会有核心数组这一个结构, 所以这里会抛出异常
        //byte[] directBufferArray = directByteBuffer.array();
        byteBuffer.position(2);
        ByteBuffer newByteBuffer = byteBuffer.slice();
        // newByteBuffer是基于原ByteBuffer的一个切片, 底层数据存储结构还是原来的byteBuffer的一个数组, 两者共享数据
        // 所以这里获取切片之后的offset的时候, 就是2, 原bytebuffer的偏移量是0, 新bytebuffer的第一个元素是原数组索引下标为2的元素
        int arrayOffset = newByteBuffer.arrayOffset();
        System.out.println(arrayOffset);
        byte[] newArray = newByteBuffer.array();
        System.out.println("array: " + array.length);
        System.out.println("newArray: "  + newArray.length);

        array[0] = 1;
        // 两个底层数组是同一个数据, 所以修改了一个数组的元素, 另外一个数组的内容也会随着一起变化
        System.out.println("newArray[0] = 1: " + newArray[0]);


    }

}
