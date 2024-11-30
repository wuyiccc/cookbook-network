package com.wuyiccc.cookbook.network.hellonetty.util.concurrent;

import java.util.Arrays;

/**
 * @author wuyiccc
 * @date 2024/11/27 08:40
 * 监听器的默认实现类, 实际上该类只是对监听器进行了一层包装, 内部持有一个监听器的数组, 向promise添加的监听器最终都添加到该类的数组中
 */
public class DefaultFutureListeners {


    private GenericFutureListener<? extends Future<?>>[] listeners;

    private int size;

    private int progressiveSize;

    DefaultFutureListeners(GenericFutureListener<? extends Future<?>> first, GenericFutureListener<? extends Future<?>> second) {

        listeners = new GenericFutureListener[2];
        listeners[0] = first;
        listeners[1] = second;
        size = 2;

        if (first instanceof GenericProgressiveFutureListener) {
            progressiveSize++;
        }

        if (second instanceof GenericProgressiveFutureListener) {
            progressiveSize++;
        }
    }


    public void add(GenericFutureListener<? extends Future<?>> l) {

        GenericFutureListener<? extends Future<?>>[] listeners = this.listeners;
        final int size = this.size;
        // 如果容量已经达到数组的上限, 那么进行*2扩容
        if (size == listeners.length) {
            this.listeners = listeners = Arrays.copyOf(listeners, size << 1);
        }

        listeners[size] = l;
        this.size = size + 1;

        if (l instanceof GenericProgressiveFutureListener) {
            progressiveSize++;
        }
    }

    public void remove(GenericFutureListener<? extends Future<?>> l) {


        final GenericFutureListener<? extends Future<?>>[] listeners = this.listeners;
        int size = this.size;
        for (int i = 0; i < size; i++) {

            if (listeners[i] == l) {
                // 计算出需要向前移动的元素个数
                int listenersToMove = size - 1 - i;
                if (listenersToMove > 0) {
                    // 利用系统拷贝移动数组元素
                    System.arraycopy(listeners, i + 1, listeners, i, listenersToMove);
                }
                // 移动之后将最后一个元素变为null
                listeners[--size] = null;
                this.size = size;

                if (l instanceof GenericProgressiveFutureListener) {
                    progressiveSize--;
                }
                return;
            }
        }
    }


    public GenericFutureListener<? extends Future<?>>[] listeners() {
        return listeners;
    }

    public int size() {
        return size;
    }

    public int progressiveSize() {

        return progressiveSize;
    }

}
