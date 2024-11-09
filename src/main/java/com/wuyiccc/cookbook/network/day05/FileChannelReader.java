package com.wuyiccc.cookbook.network.day05;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author wuyiccc
 * @date 2024/11/9 21:18
 * <p>
 * 读取文件数据
 */
public class FileChannelReader {

    public static void main(String[] args) throws IOException {

        FileInputStream in = new FileInputStream("src/main/resources/test1.txt");

        FileChannel channel = in.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(11);
        channel.read(buffer);
        buffer.flip();

        for (int i = 0; i < 11; i++) {

            System.out.println((char) buffer.get());
        }

        // 关闭in和channel中的其中一个, in/channel都会被级联关闭, 所以这里其实只需要close其中一个即可
        channel.close();
        in.close();

    }
}
