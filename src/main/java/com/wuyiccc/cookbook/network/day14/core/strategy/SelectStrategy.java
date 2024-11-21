package com.wuyiccc.cookbook.network.day14.core.strategy;

import com.wuyiccc.cookbook.network.day14.core.strategy.IntSupplier;

/**
 * @author wuyiccc
 * @date 2024/11/21 19:30
 */
public interface SelectStrategy {

    int SELECT = -1;

    int CONTINUE = -2;

    int BUSY_WAIT = -3;


    // 计算当前select策略
    int calculateStrategy(IntSupplier selectSupplier, boolean hasTasks) throws Exception;
}
