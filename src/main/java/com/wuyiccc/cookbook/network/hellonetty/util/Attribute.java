package com.wuyiccc.cookbook.network.hellonetty.util;

/**
 * @author wuyiccc
 * @date 2024/12/7 18:27
 */
public interface Attribute<T> {


    AttributeKey<T> key();

    T get();

    void set(T value);

    T getAndSet(T value);


    T setIfAbsent(T value);


    @Deprecated
    T getAndRemove();


    boolean compareAndSet(T oldValue, T newValue);

    @Deprecated
    void remove();

}
