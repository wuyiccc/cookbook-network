package com.wuyiccc.cookbook.network.hellonetty.util.concurent;

/**
 * @author wuyiccc
 * @date 2024/11/26 13:13
 */
public interface GenericProgressiveFutureListener<F extends ProgressiveFuture<?>> extends GenericFutureListener<F> {

    void operationProgressed(F future, long progress, long total) throws Exception;
}
