package com.wuyiccc.cookbook.network.mydemo.mydemo.demo07;

import io.netty.util.internal.shaded.org.jctools.queues.MpscArrayQueue;
import sun.nio.cs.ext.MS874;

/**
 * @author wuyiccc
 * @date 2025/1/14 18:34
 */
public class MpscArrayQueueTest {


    public static void main(String[] args) {

        MpscArrayQueue<String> mpscArrayQueue = new MpscArrayQueue<>(2);

        mpscArrayQueue.offer("1");

        mpscArrayQueue.offer("2");

        String poll = mpscArrayQueue.poll();
        System.out.println(poll);
    }
}
