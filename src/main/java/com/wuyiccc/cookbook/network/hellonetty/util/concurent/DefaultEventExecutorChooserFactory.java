package com.wuyiccc.cookbook.network.hellonetty.util.concurent;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wuyiccc
 * @date 2024/11/21 20:36
 *
 *  执行器选择工厂的实现类
 */
public class DefaultEventExecutorChooserFactory implements EventExecutorChooserFactory{


    public static final DefaultEventExecutorChooserFactory INSTANCE = new DefaultEventExecutorChooserFactory();

    private DefaultEventExecutorChooserFactory() {

    }

    @Override
    public EventExecutorChooser newChooser(EventExecutor[] executors) {

        if (isPowerOfTwo(executors.length)) {
            return new PowerOfTwoEventExecutorChooser(executors);
        } else {
            return new GenericEventExecutorChooser(executors);
        }
    }

    // 判断 val是否是2的次方
    private static boolean isPowerOfTwo(int val) {
        return (val & -val) == val;
    }

    private static final class PowerOfTwoEventExecutorChooser implements EventExecutorChooser {

        private final AtomicInteger idx = new AtomicInteger();

        private final EventExecutor[] executors;

        public PowerOfTwoEventExecutorChooser(EventExecutor[] executors) {
            this.executors = executors;
        }


        @Override
        public EventExecutor next() {

            // length必须是2的次方, 2^x - 1 之后全部是 1111111... 那么按位与就相当于取模, 效果等于轮询
            return executors[idx.getAndIncrement() & (executors.length - 1)];
        }
    }


    private static final class GenericEventExecutorChooser implements EventExecutorChooser {

        private final AtomicInteger idx = new AtomicInteger();

        private final EventExecutor[] executors;

        GenericEventExecutorChooser(EventExecutor[] executors) {

            this.executors = executors;
        }

        @Override
        public EventExecutor next() {

            return executors[Math.abs(idx.getAndIncrement() % executors.length)];
        }
    }
}
