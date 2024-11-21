package com.wuyiccc.cookbook.network.day14.core.strategy.factory;

import com.wuyiccc.cookbook.network.day14.core.strategy.DefaultSelectStrategy;
import com.wuyiccc.cookbook.network.day14.core.strategy.SelectStrategy;

/**
 * @author wuyiccc
 * @date 2024/11/21 19:36
 * <p>
 * 默认的选择工厂类, 由这个类创建SelectStrategy, select真正的选择策略是由该SelectStrategy接口的实现类来实现的
 */
public class DefaultSelectStrategyFactory implements SelectStrategyFactory {

    public static final SelectStrategyFactory INSTANCE = new DefaultSelectStrategyFactory();

    private DefaultSelectStrategyFactory() {

    }

    @Override
    public SelectStrategy newSelectStrategy() {

        return DefaultSelectStrategy.INSTANCE;
    }
}
