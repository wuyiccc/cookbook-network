package com.wuyiccc.cookbook.network.day09.handler.coder;

import com.wuyiccc.cookbook.network.day09.serialize.HessianSerialize;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author wuyiccc
 * @date 2024/11/15 07:14
 */
public class RpcDecoder extends ByteToMessageDecoder {


    private static final int MESSAGE_BYTES_LENGTH = 4;

    private static final int MESSAGE_LENGTH_NORMAL_LENGTH = 0;

    private Class<?> targetClass;


    public RpcDecoder(Class<?> targetClass) {
        this.targetClass = targetClass;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {


        if (in.readableBytes() < MESSAGE_BYTES_LENGTH) {
            return;
        }

        // 标记一下当前读指针, 方便做粘包拆包处理
        in.markReaderIndex();

        int messageLength = in.readInt();

        if (messageLength < MESSAGE_LENGTH_NORMAL_LENGTH) {
            ctx.close();
        }

        if (in.readableBytes() < messageLength) {
            // 如果ByteBuf当前可读区域小于数据长度, 那么重置信息, 留给下一次读取
            in.resetReaderIndex();
            return;
        }

        byte[] bytes = new byte[messageLength];

        in.readBytes(bytes);
        Object deserialize = HessianSerialize.deserialize(bytes, targetClass);
        out.add(deserialize);
    }
}
