package com.wuyiccc.cookbook.network.hellonetty.channel;

import com.wuyiccc.cookbook.network.hellonetty.util.internal.ObjectUtil;
import com.wuyiccc.cookbook.network.hellonetty.util.internal.StringUtil;

import java.lang.reflect.Constructor;

/**
 * @author wuyiccc
 * @date 2024/11/30 22:09
 */
public class ReflectiveChannelFactory<T extends Channel> implements ChannelFactory<T> {

    private final Constructor<? extends T> constructor;

    public ReflectiveChannelFactory(Class<? extends T> clazz) {

        ObjectUtil.checkNotNull(clazz, "clazz");

        try {

            this.constructor = clazz.getConstructor();
        } catch (NoSuchMethodException e) {

            throw new IllegalArgumentException("Class " + StringUtil.simpleClassName(clazz) + "does not have a public non-arg constructor", e);
        }
    }

    @Override
    public T newChannel() {

        try {

            return constructor.newInstance();
        } catch (Throwable t) {

            throw new RuntimeException("Unable to create Channel from class " + constructor.getDeclaringClass(), t);
        }
    }

    @Override
    public String toString() {

        return StringUtil.simpleClassName(ReflectiveChannelFactory.class)
                + "(" + StringUtil.simpleClassName(constructor.getDeclaringClass()) + ".class";
    }
}
