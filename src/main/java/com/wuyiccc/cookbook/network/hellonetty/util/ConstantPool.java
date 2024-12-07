package com.wuyiccc.cookbook.network.hellonetty.util;

import com.wuyiccc.cookbook.network.hellonetty.util.internal.ObjectUtil;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wuyiccc
 * @date 2024/12/7 14:59
 */
public abstract class ConstantPool<T extends Constant<T>> {


    private final ConcurrentHashMap<String, T> constants = new ConcurrentHashMap<>();


    /**
     * 初始化常量类的id, 初始值为1
     */
    private final AtomicInteger nextId = new AtomicInteger(1);


    public T valueOf(Class<?> firstNameComponent, String secondNameComponent) {

        if (firstNameComponent == null) {

            throw new NullPointerException("firstNameComponent");
        }

        if (secondNameComponent == null) {

            throw new NullPointerException("secondNameComponent");
        }

        return valueOf(firstNameComponent.getName() + '#' + secondNameComponent);
    }

    public T valueOf(String name) {

        checkNotNullAndNotEmpty(name);

        return getOrCreate(name);
    }

    /**
     * 真正创建常量类的方法, 这里的参数就是常量的名字，创建的常量是以key-name, value-ChannelOption<T>的形式
     * 存储在map中
     */
    private T getOrCreate(String name) {

        T constant = constants.get(name);

        // 先判断常量池中是否有该常量
        if (constant == null) {
            // 没有的话就创建一个
            final T tempConstant = newConstant(nextId(), name);
            // 然后放进常量池中
            constant = constants.putIfAbsent(name, tempConstant);
            if (constant == null) {
                return tempConstant;
            }
        }

        // 然后放进常池中
        return constant;
    }

    /**
     * 判断常量是否存在, 也就是常量池中是否有常量的key
     */
    public boolean exists(String name) {

        checkNotNullAndNotEmpty(name);
        return constants.containsKey(name);
    }

    /**
     * 创建常量的方法
     */
    public T newInstance(String name) {

        checkNotNullAndNotEmpty(name);
        return createOrThrow(name);
    }

    /**
     * 同样创建常量, 但是如果常量已经被创建了, 那么就会抛出异常
     */
    private T createOrThrow(String name) {

        T constant = constants.get(name);
        if (constant == null) {

            final T tempConstant = newConstant(nextId(), name);
            constant = constants.putIfAbsent(name, tempConstant);
            if (constant == null) {
                return tempConstant;
            }
        }

        throw new IllegalArgumentException(String.format("'%s' is already in use", name));
    }

    private static String checkNotNullAndNotEmpty(String name) {

        ObjectUtil.checkNotNull(name, "name");

        if (name.isEmpty()) {
            throw new IllegalArgumentException("empty name");
        }

        return name;
    }

    protected abstract T newConstant(int id, String name);


    @Deprecated
    public final int nextId() {

        return nextId.getAndIncrement();
    }
}
