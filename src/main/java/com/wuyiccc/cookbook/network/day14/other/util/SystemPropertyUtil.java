package com.wuyiccc.cookbook.network.day14.other.util;

import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * @author wuyiccc
 * @date 2024/11/20 22:57
 */
public class SystemPropertyUtil {

    private SystemPropertyUtil() {

    }


    public static String get(String key) {

        return get(key, null);
    }



    public static String get(final String key, String def) {

        if (key == null) {
            throw new NullPointerException("key");
        }

        if (key.isEmpty()) {
            throw new IllegalArgumentException("key must not be empty");
        }

        String value;

        try {

            if (System.getSecurityManager() == null) {
                value = System.getProperty(key);
            } else {
                // 如果配置的安全检查, 那么通过AccessController.doPrivileged的时候, 可以让A.jar调用B.jar的时候, 将B.jar的权限当做A.jar自己的权限
                value = AccessController.doPrivileged((PrivilegedAction<String>) () -> System.getProperty(key));
            }
        } catch (SecurityException e) {

            throw new RuntimeException(e.getMessage());
        }

        if (value == null) {
            return def;
        }

        return value;
    }

    public static int getInt(String key, int def) {

        String value = get(key);
        if (value == null) {
            return def;
        }

        value = value.trim();
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            // ignore exception
        }
        return def;
    }
}
