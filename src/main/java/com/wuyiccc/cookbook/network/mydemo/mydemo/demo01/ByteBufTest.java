package com.wuyiccc.cookbook.network.mydemo.mydemo.demo01;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.ReferenceCountUpdater;

public class ByteBufTest {

    public static void main(String[] args) {

        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(6, 10);

        buffer.writeBytes(new byte[] {1, 2, 3, 4, 5, 6});

        buffer.writeBytes(new byte[] {7, 8});

        ByteBuf duplicate = buffer.duplicate();

        System.out.println(buffer.refCnt());
        System.out.println(duplicate.refCnt());
        buffer.retain();

        System.out.println(buffer.refCnt());
        System.out.println(duplicate.refCnt());

        ReferenceCountUtil.release(buffer);
        ReferenceCountUtil.release(buffer);
        ReferenceCountUtil.release(buffer);
        ReferenceCountUtil.release(buffer);

    }


}
