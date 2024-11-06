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
public class NioDemo01 {

    public static void main(String[] args) throws IOException {

        SocketAddress rama = new InetSocketAddress("localhost", 11081);

        // 通道以阻塞模式打开, 只有在真正建立连接之后才会执行后续的代码
        SocketChannel client = SocketChannel.open(rama);


        ByteBuffer buffer = ByteBuffer.allocate(74);


        // 将可以读取的数据写入与System.out连接的这个输出通道中
        WritableByteChannel output = Channels.newChannel(System.out);

        try {
            // 阻塞模式下, 如果读取失败会抛出异常, 如果返回-1, 代表数据读取结束(可能是服务端关闭): (这行代码处于阻塞模式)
            // 阻塞模式下的正常读取 n 要么大于0, 要么==-1
            int n = client.read(buffer);

            System.out.println("读取字节数量: " + n);

            while (n > -1) {

                System.out.println("客户端读取到服务端数据");

                // 先回绕缓存区, 然后执行写入
                buffer.flip();
                // 缓冲区默认记住了自己的包含的字节数量, 不过一般情况下, 输出通道不保证会写入缓冲区所有的字节
                // 不过, 当前是阻塞通道，要么写入全部字节, 要么抛出一个IOException
                output.write(buffer);


                // 清空buffer, 与flip不同, flip可以保持缓冲区的数据不变, 只是准备写入而不是读取
                // clear则是将缓冲区重置回初始状态(但是老数据仍然存在, 只是没有被覆盖而已)
                buffer.clear();
                n = client.read(buffer);
            }
        } catch (IOException e) {
            System.err.println("链接异常: " + e.getMessage());
        }





    }


}
