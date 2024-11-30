package com.wuyiccc.cookbook.network.hellonetty.util.concurrent;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

/**
 * @author wuyiccc
 * @date 2024/11/21 19:13
 *
 * 线程创建执行器
 */
@Slf4j
public class ThreadPerTaskExecutor implements Executor {

    private final ThreadFactory threadFactory;


    public ThreadPerTaskExecutor(ThreadFactory threadFactory) {

        if (threadFactory == null) {
            throw new NullPointerException("threadFactory");
        }

        this.threadFactory = threadFactory;
    }

    @Override
    public void execute(Runnable command) {

        // 在这里创建线程并启动
        threadFactory.newThread(command).start();

        log.info(">>> 真正执行任务的线程被创建了!");
    }

}
