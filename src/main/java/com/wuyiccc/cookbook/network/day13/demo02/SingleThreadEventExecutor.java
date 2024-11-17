package com.wuyiccc.cookbook.network.day13.demo02;

import lombok.extern.slf4j.Slf4j;

import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author wuyiccc
 * @date 2024/11/17 19:27
 */
@Slf4j
public abstract class SingleThreadEventExecutor implements Executor {


    protected static final int DEFAULT_MAX_PENDING_TASKS = Integer.MAX_VALUE;

    // register event 任务队列
    private final Queue<Runnable> taskQueue;

    private final RejectedExecutionHandler rejectedExecutionHandler;

    // 标识线程是否已经启动, 防止重复启动
    private volatile boolean start = false;


    // 存储当前正在执行任务线程对象的引用
    private Thread thread;


    public SingleThreadEventExecutor() {

        this.taskQueue = newTaskQueue(DEFAULT_MAX_PENDING_TASKS);
        this.rejectedExecutionHandler = new ThreadPoolExecutor.AbortPolicy();
    }

    private Queue<Runnable> newTaskQueue(int maxPendingTasks) {

        return new LinkedBlockingQueue<>(maxPendingTasks);
    }


    /**
     * 接收SockChannel注册任务
     */
    @Override
    public void execute(Runnable task) {

        if (task == null) {
            throw new NullPointerException("task");
        }

        // 将任务提交到任务队列中
        addTask(task);
        // 启动单线程执行器
        startThread();
    }


    private void addTask(Runnable task) {

        if (task == null) {
            throw new NullPointerException("task");
        }

        // 如果向任务队列中添加任务失败, 则执行拒绝策略
        if (!offerTask(task)) {
            // 执行拒绝策略
            reject(task);
        }
    }

    private boolean offerTask(Runnable task) {

        return taskQueue.offer(task);
    }

    private void reject(Runnable task) {

        //rejectedExecutionHandler.rejectedExecution();
    }


    private void startThread() {


        // 如果之前启动过了, 那么直接返回
        if (start) {
            return;
        }

        start = true;

        new Thread(() -> {
            // 创建了新的线程
            thread = Thread.currentThread();
            // 执行run方法, 在run方法中对register和io事件进行处理
            SingleThreadEventExecutor.this.run();
        }).start();
        log.info("新线程创建了");
    }

    protected abstract void run();


    // 检查任务队列中是否还有剩余任务
    protected boolean hasTasks() {

        return !taskQueue.isEmpty();
    }


    // 执行所有的任务
    protected void runAllTasks() {

        runAllTasksFrom(taskQueue);
    }


    private void runAllTasksFrom(Queue<Runnable> taskQueue) {

        Runnable task = pollTaskFrom(taskQueue);

        if (task == null) {
            return;
        }

        for (; ; ) {

            // 执行任务队列中的任务
            safeExecute(task);

            task = pollTaskFrom(taskQueue);

            if (task == null) {
                return;
            }

        }
    }


    private Runnable pollTaskFrom(Queue<Runnable> taskQueue) {

        return taskQueue.poll();
    }

    private void safeExecute(Runnable task) {
        try {
            task.run();
        } catch (Exception e) {
            log.warn("A task raised an exception. Task: {}", task, e);
        }
    }

    // 判断当前执行任务的线程是否是执行器的线程
    public boolean inEventLoop(Thread thread) {
        return thread == this.thread;
    }

}
