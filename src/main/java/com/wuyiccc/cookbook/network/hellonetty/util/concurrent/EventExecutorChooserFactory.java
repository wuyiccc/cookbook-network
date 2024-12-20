package com.wuyiccc.cookbook.network.hellonetty.util.concurrent;

/**
 * @author wuyiccc
 * @date 2024/11/21 20:32
 *
 * 执行器选择工厂接口
 */
public interface EventExecutorChooserFactory {

    EventExecutorChooser newChooser(EventExecutor[] executors);


    interface EventExecutorChooser {

        EventExecutor next();
    }
}
