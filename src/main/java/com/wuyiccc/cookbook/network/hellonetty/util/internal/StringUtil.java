package com.wuyiccc.cookbook.network.hellonetty.util.internal;

/**
 * @author wuyiccc
 * @date 2024/11/20 21:50
 */
public class StringUtil {

    public static String simpleClassName(Class<?> clazz) {

        return clazz.getSimpleName();
    }

    public static String simpleClassName(Object o) {
        if (o == null) {
            return "null_object";
        } else {
            return simpleClassName(o.getClass());
        }

    }

}
