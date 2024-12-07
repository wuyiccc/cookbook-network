package com.wuyiccc.cookbook.network.hellonetty.channel;

import java.util.Map;

/**
 * @author wuyiccc
 * @date 2024/11/30 20:28
 */
public interface ChannelConfig {


    Map<ChannelOption<?>, Object> getOptions();

    boolean setOptions(Map<ChannelOption<?>, ?> options);

    <T> T getOption(ChannelOption<T> option);

    <T> boolean setOption(ChannelOption<T> option, T value);

    int getConnectTimeoutMillis();

    ChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis);

    int getWriteSpinCount();

    ChannelConfig setWriteSpinCount(int writeSpinCount);

    boolean isAutoRead();

    ChannelConfig setAutoRead(boolean autoRead);

    boolean isAutoClose();

    ChannelConfig setAutoClose(boolean autoClose);

    int getWriteBufferHighWaterMark();

    ChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark);

    int getWriteBufferLowWaterMark();

    ChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark);
}
