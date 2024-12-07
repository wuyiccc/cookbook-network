package com.wuyiccc.cookbook.network.hellonetty.util;

/**
 * @author wuyiccc
 * @date 2024/12/7 18:59
 */
public interface AttributeMap {

    <T> Attribute<T> attr(AttributeKey<T> key);

    <T> boolean hasAttr(AttributeKey<T> key);
}
