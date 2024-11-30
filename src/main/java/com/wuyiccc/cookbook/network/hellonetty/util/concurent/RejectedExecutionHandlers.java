package com.wuyiccc.cookbook.network.hellonetty.util.concurent;

import java.util.concurrent.RejectedExecutionException;

/**
 * @author wuyiccc
 * @date 2024/11/21 19:59
 *
 * 创建拒绝策略处理器
 */
public class RejectedExecutionHandlers {

    private static final RejectedExecutionHandler REJECT = (task, executor) -> {

        throw new RejectedExecutionException();
    };

    private RejectedExecutionHandlers() {

    }

    public static RejectedExecutionHandler reject() {
        return REJECT;
    }
}
