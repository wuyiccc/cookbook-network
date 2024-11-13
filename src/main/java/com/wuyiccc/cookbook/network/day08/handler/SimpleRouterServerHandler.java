package com.wuyiccc.cookbook.network.day08.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author wuyiccc
 * @date 2024/11/13 22:09
 */
public class SimpleRouterServerHandler extends SimpleChannelInboundHandler {

    static ExecutorService executorService = Executors.newSingleThreadExecutor();

    PooledByteBufAllocator allocator = new PooledByteBufAllocator(false);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf reqMsg = (ByteBuf) msg;

        byte[] body = new byte[reqMsg.readableBytes()];

        // 继承SimpleChannelInboundHandler，不用主动释放reqMsg, 在channelRead0执行完毕之后, SimpleHandler会自动释放msg对象
        //ReferenceCountUtil.release(reqMsg);

        executorService.execute(() -> {

            // 申请新的ByteBuf, 这里因为数据向后传递了, 就算后面的handler没有主动释放ByteBuf, 那么tailContext也会做一层兜底进行释放ByteBuf
            ByteBuf respMsg = allocator.heapBuffer(body.length);
            respMsg.writeBytes(body);
            ctx.writeAndFlush(respMsg);
        });
    }
}
