package com.wuyiccc.cookbook.network.day15;

import java.util.concurrent.*;

/**
 * @author wuyiccc
 * @date 2024/11/23 22:42
 */
public class FutureTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        demo03();
    }


    public static void demo01() throws ExecutionException, InterruptedException {

        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {

                return 1214;
            }
        };

        FutureTask<Integer> future = new FutureTask<>(callable);

        // 创建一个线程
        Thread t = new Thread(future);
        t.start();

        // 无超时获取结果
        System.out.println(future.get());
    }

    public static void demo02() throws ExecutionException, InterruptedException {

        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {

                return 1214;
            }
        };


        ExecutorService threadPool = Executors.newCachedThreadPool();
        Future<?> future = threadPool.submit(callable);

        System.out.println(future.get());
    }

    public static void demo03() throws ExecutionException, InterruptedException {

        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {

                TimeUnit.SECONDS.sleep(10);
                return 1214;
            }
        };

        FutureTask<Integer> futureTask = new FutureTask<>(callable);

        ExecutorService threadPool = Executors.newCachedThreadPool();
        Future<?> newFutureTask = threadPool.submit(futureTask);

        System.out.println(newFutureTask.get());
        System.out.println(futureTask.get());
    }

}
