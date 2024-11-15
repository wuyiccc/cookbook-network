package com.wuyiccc.cookbook.network.day09.handler.coder;

import com.wuyiccc.cookbook.network.day09.serialize.HessianSerialize;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author wuyiccc
 * @date 2024/11/14 22:13
 */
public class RpcEncoder extends MessageToByteEncoder {

    private Class<?> targetClass;


    public RpcEncoder(Class<?> targetClass) {
        this.targetClass = targetClass;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {


        if (targetClass.isInstance(o)) {

            byte[] bytes = HessianSerialize.serialize(o);

            byteBuf.writeInt(bytes.length);
            byteBuf.writeBytes(bytes);
        }
    }



}
