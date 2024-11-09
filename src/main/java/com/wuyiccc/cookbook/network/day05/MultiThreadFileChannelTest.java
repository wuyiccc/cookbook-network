package com.wuyiccc.cookbook.network.day05;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

/**
 * @author wuyiccc
 * @date 2024/11/9 21:11
 */
public class MultiThreadFileChannelTest {

    public static void main(String[] args) throws FileNotFoundException {


        FileOutputStream out = new FileOutputStream("src/main/resources/test1.txt");

        FileChannel channel = out.getChannel();

        ByteBuffer buffer = ByteBuffer.wrap("hello world!".getBytes(StandardCharsets.UTF_8));

        for (int i = 0; i < 10; i++) {

            new Thread(() -> {
                try {
                    channel.write(buffer.duplicate());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }


    }
}
