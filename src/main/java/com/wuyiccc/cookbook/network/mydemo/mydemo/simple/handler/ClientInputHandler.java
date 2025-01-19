package com.wuyiccc.cookbook.network.mydemo.mydemo.simple.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * @author wuyiccc
 * @date 2025/1/19 13:35
 */
public class ClientInputHandler extends ChannelInboundHandlerAdapter {

    private Thread thread;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        System.out.println("我已经链接上服务端了, 开启打印机线程");

        this.thread = new Thread(() -> {

            // 读取console的数据
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                try {
                    String data = reader.readLine();
                    // 输入数据
                    ByteBuf byteBuf = ctx.alloc().buffer();
                    byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
                    byteBuf.writeInt(dataBytes.length);
                    byteBuf.writeBytes(dataBytes);

                    byteBuf.writeInt(3);
                    byteBuf.writeBytes("end".getBytes(StandardCharsets.UTF_8));
                    ctx.channel().writeAndFlush(byteBuf);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        });
        this.thread.start();
    }




}
