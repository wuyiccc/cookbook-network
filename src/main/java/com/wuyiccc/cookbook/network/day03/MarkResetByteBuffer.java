package com.wuyiccc.cookbook.network.day03;

import java.nio.ByteBuffer;

/**
 * @author wuyiccc
 * @date 2024/11/9 14:42
 */
public class MarkResetByteBuffer {

    public static void main(String[] args) {

        ByteBuffer byteBuffer = ByteBuffer.allocate(100);

        byteBuffer.position(10);

        // 此刻mark=15
        byteBuffer.mark();

        byteBuffer.position(25);

        System.out.println(byteBuffer.position());

        // reset重置, 另外一个意思可以理解为回退position, 因为手动指定position的值小于前面的mark的时候, mark又会被重置为-1
        // 只有当position的值在前进的时候, mark=旧的position才有效
        byteBuffer.reset();

        System.out.println(byteBuffer.position());


    }
}
