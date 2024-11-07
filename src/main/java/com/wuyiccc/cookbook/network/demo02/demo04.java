package com.wuyiccc.cookbook.network.demo02;

import java.nio.CharBuffer;

/**
 * @author wuyiccc
 * @date 2024/11/7 21:59
 */
public class demo04 {

    public static void main(String[] args) {

        CharBuffer charBuffer = CharBuffer.allocate(100);
        // 仅仅只是在底层数组对应的位置上设置了'a'这个元素而已, 并且修改了position的位置
        charBuffer.put('a');

        //char c = charBuffer.get();
        //System.out.println(c);

        // 方法一: 如果想要读取元素, 可以使用flip回绕缓冲区
        // limit = position, position = 0, mark = -1
        //charBuffer.flip();
        //System.out.println(charBuffer.get());

        // 方法二: 除了使用flip之外, 底层get()方法其实是获取position位置上面的元素的值, 然后将position+1
        // 知道这个底层逻辑之后, 我们可以将position设置为0, 那么使用get方法, 同样可以获取到index=0的元素
        //charBuffer.position(0);
        //System.out.println(charBuffer.get());


        // 方法三: 可以使用指定位置获取元素, 这样不会对byteBuffer的指标造成影响
        // 等价于直接获取底层数组指定位置上的元素
        System.out.println(charBuffer.get(0));

        // 等价于直接给底层数组上指定索引位置设置值, 而不会改变byteBuffer指标参数的值
        charBuffer.put(1, 'b');

        // 批量设置数据
        String data = "nihao";
        charBuffer.mark();
        // 相当于循环调用put(char)方法, 设置数据的同时会递增position的值
        // 如果缓冲区没有足够的空间容纳这个数据, 那么就会抛出BufferOverflowException异常
        charBuffer.put(data, 0, data.length());

        char[] newData = new char[5];
        // 读取之前先将position的位置重置到设置数据之前的位置, 这个时候position的下标指向nihao中第一个字符n
        charBuffer.reset();
        // 从position开始, 读取数据到newData中, 可读取的数量必须 >= newData的容量
        // 如果没有足够的剩余数据塞满newData, 那么就会抛出BufferOverflowException的异常
        charBuffer.get(newData);

        for (int i = 0; i < newData.length; i++) {
            System.out.print(newData[i]);
        }
        System.out.println();
    }
}
