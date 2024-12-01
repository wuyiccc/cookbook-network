package com.wuyiccc.cookbook.network.hellonetty.channel.nio;

import com.wuyiccc.cookbook.network.hellonetty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

/**
 * @author wuyiccc
 * @date 2024/12/1 16:28
 */
@Slf4j
public abstract class AbstractNioByteChannel extends AbstractNioChannel {

    protected AbstractNioByteChannel(Channel parent, SelectableChannel ch) {
        super(parent, ch, SelectionKey.OP_READ);
    }

    // 客户端channel读取数据的方法
    @Override
    public final void read() {

        ByteBuffer byteBuf = ByteBuffer.allocate(1024);

        try {
            doReadBytes(byteBuf);
        } catch (Exception e) {
            log.error("read失败", e);
        }
    }


    protected abstract int doReadBytes(ByteBuffer buf) throws Exception;
}
