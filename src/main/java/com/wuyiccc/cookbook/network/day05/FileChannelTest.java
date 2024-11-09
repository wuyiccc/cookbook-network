package com.wuyiccc.cookbook.network.day05;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

/**
 * @author wuyiccc
 * @date 2024/11/9 18:47
 */
public class FileChannelTest {

    public static void main(String[] args) throws IOException {

        ByteBuffer buffer = ByteBuffer.wrap("hello      world".getBytes(StandardCharsets.UTF_8));
        ByteBuffer bufferNew = ByteBuffer.wrap("your".getBytes(StandardCharsets.UTF_8));


        FileOutputStream out = new FileOutputStream("src/main/resources/test1.txt");
        FileChannel channel = out.getChannel();


        channel.write(buffer);

        System.out.println(buffer);

        channel.position(6);

        channel.write(bufferNew);


        channel.close();
        out.close();
    }
}
