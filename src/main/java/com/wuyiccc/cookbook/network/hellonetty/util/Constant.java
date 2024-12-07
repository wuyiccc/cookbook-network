package com.wuyiccc.cookbook.network.hellonetty.util;

/**
 * @author wuyiccc
 * @date 2024/12/7 14:48
 *
 * 自限定泛型
 */
public interface Constant<T extends Constant<T>> extends Comparable<T> {

    int id();

    String name();

}
