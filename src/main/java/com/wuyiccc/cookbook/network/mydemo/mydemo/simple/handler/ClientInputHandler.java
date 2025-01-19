package com.wuyiccc.cookbook.network.mydemo.mydemo.simple.handler;

import com.wuyiccc.cookbook.network.mydemo.mydemo.simple.request.SimpleRequest;
import com.wuyiccc.cookbook.network.mydemo.mydemo.simple.serialize.CustomSerialize;
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
            int i = 0;
            while (true) {
                try {
                    String data = reader.readLine();

                    SimpleRequest simpleRequest = new SimpleRequest();
                    simpleRequest.setContent(data);
                    simpleRequest.setE(new RuntimeException("测试: " + i));
                    byte[] dataBytes = CustomSerialize.serialize(simpleRequest);

                    // 输入数据
                    ByteBuf byteBuf = ctx.alloc().buffer();
                    byteBuf.writeInt(dataBytes.length);
                    byteBuf.writeBytes(dataBytes);

                    ctx.channel().writeAndFlush(byteBuf);

                    i++;

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        });
        this.thread.start();
    }




}
