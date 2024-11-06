package com.wuyiccc.cookbook.network.demo01;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;

/**
 * @author wuyiccc
 * @date 2024/11/6 19:56
 */
public class NioDemo02 {

    public static void main(String[] args) throws IOException {

        SocketAddress rama = new InetSocketAddress("localhost", 10091);

        // 通道以阻塞模式打开, 只有在真正建立连接之后才会执行后续的代码
        SocketChannel client = SocketChannel.open(rama);

        // 设置客户端运行在非阻塞模式
        client.configureBlocking(false);


        ByteBuffer buffer = ByteBuffer.allocate(74);


        // 将可以读取的数据写入与System.out连接的这个输出通道中
        WritableByteChannel output = Channels.newChannel(System.out);

        while (true) {

            // 阻塞模式下, 如果读取失败会抛出异常, 如果返回-1, 代表数据读取结束:
            // 如果客户端处于非阻塞模式, 没有字节可用的时候会立即返回0 (这行代码处于非阻塞模式)

            // 总结: 非阻塞模式的客户端, 如果服务端正常运行有数据, 那么 n >= 1, 如果没数据则 n == 0, 如果服务端关闭则 n == -1
            int n = client.read(buffer);

            if (n > 0) {
                // 先回绕缓存区, 然后执行写入
                buffer.flip();
                // 缓冲区默认记住了自己的包含的字节数量, 不过一般情况下, 输出通道不保证会写入缓冲区所有的字节
                // 不过, 当前是阻塞通道，要么写入全部字节, 要么抛出一个IOException
                output.write(buffer);


                // 清空buffer, 与flip不同, flip可以保持缓冲区的数据不变, 只是准备写入而不是读取
                // clear则是将缓冲区重置回初始状态(但是老数据仍然存在, 只是没有被覆盖而已)
                buffer.clear();
            } else if (n == -1) {

                // 当服务器处于故障的时候发生该情况(比如服务端连接到一半关闭)
                System.err.println("服务端异常关闭");
                break;
            }


        }




    }


}
