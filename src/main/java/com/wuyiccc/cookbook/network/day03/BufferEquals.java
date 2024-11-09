package com.wuyiccc.cookbook.network.day03;

import java.nio.CharBuffer;

/**
 * @author wuyiccc
 * @date 2024/11/9 14:50
 */
public class BufferEquals {

    public static void main(String[] args) {

        CharBuffer b1 = CharBuffer.wrap("12345678");
        CharBuffer b2 = CharBuffer.wrap("5678");

        b1.get();
        b1.get();
        b1.get();
        b1.get();

        // 如果 b1 == b2  => true
        // 两者属于同一个CharBuffer类型的对象且两者 limit - position 的值相同, 且 position -> limit 之间两者的数据相同 => true
        System.out.println(b1.equals(b2));

        // hashCode生成采用h*31, 主要原因是31产生哈希冲突的概率比较小
        int res = b1.hashCode();
    }
}
