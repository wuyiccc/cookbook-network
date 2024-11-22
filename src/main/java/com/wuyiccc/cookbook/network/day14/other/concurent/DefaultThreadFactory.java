package com.wuyiccc.cookbook.network.day14.other.concurent;

import com.wuyiccc.cookbook.network.day14.other.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Locale;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wuyiccc
 * @date 2024/11/20 22:12
 *
 * 默认线程创建工厂
 */
@Slf4j
public class DefaultThreadFactory implements ThreadFactory {

    // 初始值为 0 构造DefaultThreadFactory的时候分配的线程池组的id
    private static final AtomicInteger POOL_ID = new AtomicInteger();

    // 初始值为 0 分配的线程id
    private static final AtomicInteger nextId = new AtomicInteger();

    // 分配线程名称前缀 ===> defaultThreadFactory-poolId-nextId
    private final String prefix;

    // 分配线程是否是daemon线程
    private final boolean daemon;

    // 分配线程优先级
    private final int priority;

    // 线程组
    protected final ThreadGroup threadGroup;

    public DefaultThreadFactory() {
        this(DefaultThreadFactory.class);
    }

    public DefaultThreadFactory(Class<?> poolType) {
        this(poolType, Thread.NORM_PRIORITY);
    }

    public DefaultThreadFactory(Class<?> poolType, int priority) {

        // 设置非守护线程, 优先级为5
        this(poolType, false, priority);
    }

    public DefaultThreadFactory(Class<?> poolType, boolean daemon, int priority) {
        this(toPoolName(poolType), daemon, priority);
    }

    public DefaultThreadFactory(String poolName, boolean daemon, int priority) {

        this(poolName, daemon, priority, System.getSecurityManager() == null ? Thread.currentThread().getThreadGroup() : System.getSecurityManager().getThreadGroup());
    }

    public DefaultThreadFactory(String poolName, boolean daemon, int priority, ThreadGroup threadGroup) {

        if (poolName == null) {
            throw new NullPointerException("poolName");
        }

        if (priority < Thread.MIN_PRIORITY || priority > Thread.MAX_PRIORITY) {

            throw new IllegalArgumentException("priority: " + priority + " (expected: Thread.MIN_PRIORITY <= priority <= Thread.MAX_PRIORITY");
        }

        // 给属性赋值
        prefix = poolName + '-' + POOL_ID.incrementAndGet() + '-';
        this.daemon = daemon;
        this.priority = priority;
        this.threadGroup = threadGroup;
    }


    /**
     * 获取小写驼峰开头的简单类名
     */
    public static String toPoolName(Class<?> poolType) {

        if (poolType == null) {
            throw new NullPointerException("poolType");
        }

        String poolName = StringUtil.simpleClassName(poolType);

        switch (poolName.length()) {
            case 0:
                return "unknown";
            case 1:
                return poolName.toLowerCase(Locale.US);
            default:
                if (Character.isUpperCase(poolName.charAt(0)) && Character.isLowerCase(poolName.charAt(1))) {
                    // 如果首字母是大写的, 则转为小写再返回
                    return Character.toLowerCase(poolName.charAt(0)) + poolName.substring(1);
                } else {
                    return poolName;
                }
        }
    }

    @Override
    public Thread newThread(Runnable r) {

        Thread t = new Thread(r, prefix + nextId.incrementAndGet());

        log.info(">>> 新建线程: {}", t);

        if (t.isDaemon() != daemon) {
            t.setDaemon(daemon);
        }

        if (t.getPriority() != priority) {
            t.setPriority(priority);
        }

        return t;
    }


}
