package com.wuyiccc.cookbook.network.day14.core.choose;

import com.wuyiccc.cookbook.network.day14.core.executor.EventExecutor;

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
