package com.wuyiccc.cookbook.network.hellonetty.util.concurrent;

import java.util.EventListener;

/**
 * @author wuyiccc
 * @date 2024/11/24 23:23
 */
public interface GenericFutureListener<F extends Future<?>> extends EventListener {


    void operationComplete(F future) throws Exception;
}
