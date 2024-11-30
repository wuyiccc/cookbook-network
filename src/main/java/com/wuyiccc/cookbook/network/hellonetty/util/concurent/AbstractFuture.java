package com.wuyiccc.cookbook.network.hellonetty.util.concurent;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author wuyiccc
 * @date 2024/11/26 13:26
 */
public abstract class AbstractFuture<V> implements Future<V> {

    @Override
    public V get() throws InterruptedException, ExecutionException {

        await();

        Throwable cause = cause();
        if (cause == null) {
            return getNow();
        }

        if (cause instanceof CancellationException) {
            throw (CancellationException) cause;
        }

        throw new ExecutionException(cause);
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {

        if (await(timeout, unit)) {

            Throwable cause = cause();
            if (cause == null) {
                return getNow();
            }

            if (cause instanceof CancellationException) {
                throw (CancellationException) cause;
            }

            throw new ExecutionException(cause);
        }

        throw new TimeoutException();
    }
}
