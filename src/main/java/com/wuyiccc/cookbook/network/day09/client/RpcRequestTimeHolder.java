package com.wuyiccc.cookbook.network.day09.client;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wuyiccc
 * @date 2024/11/15 22:48
 */
public class RpcRequestTimeHolder {

    private static ConcurrentHashMap<String, Long> requestTimes = new ConcurrentHashMap<>();

    public static void put(String requestId, long requestTime) {

        requestTimes.put(requestId, requestTime);
    }

    public static long get(String requestId) {

        return requestTimes.get(requestId);
    }

    public static void remove(String requestId) {
        requestTimes.remove(requestId);
    }
}
