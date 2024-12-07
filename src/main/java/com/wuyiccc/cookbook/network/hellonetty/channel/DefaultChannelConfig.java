package com.wuyiccc.cookbook.network.hellonetty.channel;


import com.wuyiccc.cookbook.network.hellonetty.util.internal.ObjectUtil;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

import static com.wuyiccc.cookbook.network.hellonetty.channel.ChannelOption.*;


/**
 * @author wuyiccc
 * @date 2024/12/7 22:39
 * 该类是NioSocketChannelConfig和NioServerSocketChannelConfig的公共父类，
 * 这里面有一些方法没做实现，之后讲到了会实现 不过，只要是该类实现了的方法，都很简单，结合本节课的测试用例debug，很顺利就可以看懂。
 */
public class DefaultChannelConfig implements ChannelConfig {

    private static final int DEFAULT_CONNECT_TIMEOUT = 30000;

    /**
     * 原子更新器，改变是否自动读的值，自动读这个属性很重要，讲到channelHandler的时候会派上用场
     */
    private static final AtomicIntegerFieldUpdater<DefaultChannelConfig> AUTOREAD_UPDATER =
            AtomicIntegerFieldUpdater.newUpdater(DefaultChannelConfig.class, "autoRead");

    protected final Channel channel;

    private volatile int connectTimeoutMillis = DEFAULT_CONNECT_TIMEOUT;

    /**
     * 写自旋次数也是个很重要的属性，默认值为16。这里我们还用不到它，先引入混个眼熟
     */
    private volatile int writeSpinCount = 16;

    private volatile int autoRead = 1;
    private volatile boolean autoClose = true;

    public DefaultChannelConfig(Channel channel) {
        this.channel = channel;
    }


    /**
     * 该方法是返回存储所有常量类和常量类对应的值的map
     */
    @Override
    public Map<ChannelOption<?>, Object> getOptions() {
        return getOptions(null, CONNECT_TIMEOUT_MILLIS, WRITE_SPIN_COUNT,
                AUTO_READ, AUTO_CLOSE, SINGLE_EVENTEXECUTOR_PER_GROUP);
    }

    protected Map<ChannelOption<?>, Object> getOptions(Map<ChannelOption<?>, Object> result, ChannelOption<?>... options) {

        if (result == null) {
            //IdentityHashMap是java自己的map，这个map允许放入相同的key，实际上是因为这个map判断相等采用的是地址值
            //地址值不同的两个对象，即便hash值相等，也可以放入map中
            result = new IdentityHashMap<>();
        }
        for (ChannelOption<?> o : options) {
            result.put(o, getOption(o));
        }
        return result;
    }


    @Override
    public boolean setOptions(Map<ChannelOption<?>, ?> options) {

        if (options == null) {
            throw new NullPointerException("options");
        }

        boolean setAllOptions = true;

        for (Map.Entry<ChannelOption<?>, ?> e : options.entrySet()) {

            if (!setOption((ChannelOption<Object>) e.getKey(), e.getValue())) {
                setAllOptions = false;
            }
        }
        return setAllOptions;
    }

    @Override
    public <T> T getOption(ChannelOption<T> option) {

        if (option == null) {
            throw new NullPointerException("option");
        }

        if (option == CONNECT_TIMEOUT_MILLIS) {
            return (T) Integer.valueOf(getConnectTimeoutMillis());
        }

        if (option == WRITE_SPIN_COUNT) {
            return (T) Integer.valueOf(getWriteSpinCount());
        }

        if (option == AUTO_READ) {
            return (T) Boolean.valueOf(isAutoRead());
        }

        if (option == AUTO_CLOSE) {
            return (T) Boolean.valueOf(isAutoClose());
        }
        return null;
    }

    /**
     * 传输进来的option，如果该option等于某个之前创建好的常量类，就把value赋值过去
     */
    @Override
    public <T> boolean setOption(ChannelOption<T> option, T value) {

        validate(option, value);

        if (option == CONNECT_TIMEOUT_MILLIS) {

            setConnectTimeoutMillis((Integer) value);
        } else if (option == WRITE_SPIN_COUNT) {

            setWriteSpinCount((Integer) value);
        } else if (option == AUTO_READ) {

            setAutoRead((Boolean) value);
        } else if (option == AUTO_CLOSE) {

            setAutoClose((Boolean) value);
        } else {

            return false;
        }
        return true;
    }

    protected <T> void validate(ChannelOption<T> option, T value) {

        if (option == null) {
            throw new NullPointerException("option");
        }
        option.validate(value);
    }

    @Override
    public int getConnectTimeoutMillis() {
        return connectTimeoutMillis;
    }

    @Override
    public ChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {

        ObjectUtil.checkPositiveOrZero(connectTimeoutMillis, "connectTimeoutMillis");
        this.connectTimeoutMillis = connectTimeoutMillis;
        return this;
    }

    @Override
    public int getWriteSpinCount() {
        return writeSpinCount;
    }

    @Override
    public ChannelConfig setWriteSpinCount(int writeSpinCount) {

        ObjectUtil.checkPositive(writeSpinCount, "writeSpinCount");

        if (writeSpinCount == Integer.MAX_VALUE) {

            --writeSpinCount;
        }

        this.writeSpinCount = writeSpinCount;
        return this;
    }

    @Override
    public boolean isAutoRead() {

        //默认为true的意思
        return autoRead == 1;
    }

    @Override
    public ChannelConfig setAutoRead(boolean autoRead) {

        boolean oldAutoRead = AUTOREAD_UPDATER.getAndSet(this, autoRead ? 1 : 0) == 1;

        if (autoRead && !oldAutoRead) {

            channel.read();
        } else if (!autoRead && oldAutoRead) {

            autoReadCleared();
        }

        return this;
    }

    protected void autoReadCleared() {
    }

    @Override
    public boolean isAutoClose() {
        return autoClose;
    }

    @Override
    public ChannelConfig setAutoClose(boolean autoClose) {
        this.autoClose = autoClose;
        return this;
    }

    @Override
    public int getWriteBufferHighWaterMark() {
        return 0;
    }

    @Override
    public ChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
        return null;
    }

    @Override
    public int getWriteBufferLowWaterMark() {
        return 0;
    }

    @Override
    public ChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
        return null;
    }
}
