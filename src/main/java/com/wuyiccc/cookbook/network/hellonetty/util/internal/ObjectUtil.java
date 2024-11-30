package com.wuyiccc.cookbook.network.hellonetty.util.internal;

/**
 * @author wuyiccc
 * @date 2024/11/20 22:54
 */
public class ObjectUtil {

    private ObjectUtil() {

    }

    public static <T> T checkNotNull(T arg, String text) {

        if (arg == null) {
            throw new NullPointerException(text);
        }

        return arg;
    }

    public static int checkPositive(int i, String name) {

        if (i <= 0) {
            throw new IllegalArgumentException(name + ":" + i + " (expected: > 0)");
        }
        return i;
    }


}
