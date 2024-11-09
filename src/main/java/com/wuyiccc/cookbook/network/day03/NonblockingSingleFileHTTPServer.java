package com.wuyiccc.cookbook.network.day03;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Set;

/**
 * @author wuyiccc
 * @date 2024/11/9 11:16
 */
public class NonblockingSingleFileHTTPServer {

    public static void main(String[] args) throws IOException {


        // 1. 创建服务端server
        ServerSocketChannel sc = ServerSocketChannel.open();
        sc.bind(new InetSocketAddress("localhost", 10091));
        sc.configureBlocking(false);

        Selector selector = Selector.open();
        // 3. 监听客户端accept
        // 4. 注册selector监听客户端请求
        sc.register(selector, SelectionKey.OP_ACCEPT);

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


        while (true) {

            selector.select();

            Set<SelectionKey> keySet = selector.selectedKeys();

            Iterator<SelectionKey> iterator = keySet.iterator();

            while (iterator.hasNext()) {

                SelectionKey key = iterator.next();
                // 移除key, 防止下次select的时候重复处理
                iterator.remove();
                try {

                    if (key.isAcceptable()) {

                        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                        SocketChannel clientSocketChannel = serverSocketChannel.accept();
                        clientSocketChannel.configureBlocking(false);

                        clientSocketChannel.register(selector, SelectionKey.OP_READ);
                    } else if (key.isReadable()) {

                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        // 忽略浏览器 http客户端发来的请求数据
                        ByteBuffer buffer = ByteBuffer.allocate(4096);
                        socketChannel.read(buffer);

                        // 将通道切换为写模式, 取消读模式的监听
                        key.interestOps(SelectionKey.OP_WRITE);
                        // 复制一个缓冲区给当前客户端的key, 当多个client并发连接的时候,
                        // 后入因为复制的缓冲区的参数指标互不影响, 所以后面可以并发写入给客户端
                        key.attach(httpDataByteBuffer.duplicate());
                    } else if (key.isWritable()) {

                        SocketChannel socketChannel = (SocketChannel) key.channel();

                        // 拿到对应的缓冲区
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        if (buffer.hasRemaining()) {
                            // 这里通过写入一个4.6mb的pdf文件可以看出, buffer的大小是4.6mb, 一次write并不能全部写入
                            // 此次write执行完毕之后, buffer的position可能会来到2.3mb的位置
                            // 剩余的数据可以留到下一次key为writable的时候来写入,
                            // 也可以在这里循环执行write方案 while(buffer.hasRemaining()) {write()}
                            socketChannel.write(buffer);
                        } else {
                            socketChannel.close();
                        }

                        // 方式二, 直接while全部写入
                        //while (buffer.hasRemaining()) {
                        //    socketChannel.write(buffer);
                        //}
                        socketChannel.close();
                    }
                } catch (Exception e) {

                    key.cancel();
                    try {
                        key.channel().close();
                    } catch (IOException ex) {

                    }
                }


            }
        }





    }
}
