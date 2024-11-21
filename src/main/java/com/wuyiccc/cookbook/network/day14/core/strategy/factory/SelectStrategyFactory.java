package com.wuyiccc.cookbook.network.day14.core.strategy.factory;

import com.wuyiccc.cookbook.network.day14.core.strategy.SelectStrategy;

/**
 * @author wuyiccc
 * @date 2024/11/21 19:37
 */
public interface SelectStrategyFactory {

    SelectStrategy newSelectStrategy();
}
