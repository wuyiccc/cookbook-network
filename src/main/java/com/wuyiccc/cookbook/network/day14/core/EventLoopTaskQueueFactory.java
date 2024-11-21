package com.wuyiccc.cookbook.network.day14.core;

import java.util.Queue;

/**
 * @author wuyiccc
 * @date 2024/11/21 19:20
 *
 * 创建任务队列的工厂
 */
public interface EventLoopTaskQueueFactory {

    Queue<Runnable> newTaskQueue(int maxCapacity);
}
