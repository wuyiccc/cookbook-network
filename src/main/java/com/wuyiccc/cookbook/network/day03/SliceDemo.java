package com.wuyiccc.cookbook.network.day03;

import java.nio.ByteBuffer;

/**
 * @author wuyiccc
 * @date 2024/11/9 14:31
 */
public class SliceDemo {

    public static void main(String[] args) {

        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 2);
        byteBuffer.put((byte) 3);
        byteBuffer.put((byte) 4);
        byteBuffer.put((byte) 5);
        byteBuffer.put((byte) 6);

        // 设置 limit=position, position=0
        byteBuffer.flip();

        // 再次设置position=1, 这样后面slice的时候, 只会拿到2,3,4,5,6的数据了
        byteBuffer.position(1);

        // slice分片缓冲区基于当前buffer的position进行截断, 与原buffer共享一个底层数组
        // 但是offset是基于原来的offset+position
        // 可以这样简单理解, slice就是截断原buffer的position~limit之间的部分(注意不是capacity)
        // 指标参数与原buffer不共享
        ByteBuffer slice = byteBuffer.slice();


        while (slice.hasRemaining()) {
            byte b = slice.get();
            System.out.println(b);
        }
    }
}
