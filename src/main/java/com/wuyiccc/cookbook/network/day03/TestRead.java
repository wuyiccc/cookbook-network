package com.wuyiccc.cookbook.network.day03;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author wuyiccc
 * @date 2024/11/9 16:52
 */
public class TestRead {

    public static void main(String[] args) throws IOException {


        // 2. 读取数据到bytebuffer
        String contentType = URLConnection.getFileNameMap().getContentTypeFor("/Users/wuxingyu/work/tmp/世纪三部曲-巨人的陨落.pdf");
        Path file = FileSystems.getDefault().getPath("/Users/wuxingyu/work/tmp/世纪三部曲-巨人的陨落.pdf");
        byte[] data = Files.readAllBytes(file);

        ByteBuffer dataByteBuffer = ByteBuffer.wrap(data);

        String header = "HTTP/1.0 200 OK\r\n"
                + "Server: NonblockingSingleFileHTTPServer\r\n"
                + "Content-length: " + dataByteBuffer.limit() + "\r\n"
                + "Content-type: " + contentType + "\r\n\r\n";

        // http头采用ascii的编码方式
        byte[] headerData = header.getBytes(StandardCharsets.US_ASCII);

        ByteBuffer httpDataByteBuffer = ByteBuffer.allocate(dataByteBuffer.limit() + headerData.length);

        httpDataByteBuffer.put(headerData);
        httpDataByteBuffer.put(dataByteBuffer);
        httpDataByteBuffer.flip();

        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("localhost", 10091));

        int write = socketChannel.write(httpDataByteBuffer);

        System.out.println(write);
    }
}
