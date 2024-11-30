package com.wuyiccc.cookbook.network.hellonetty.channel;

import com.wuyiccc.cookbook.network.hellonetty.IntSupplier;

/**
 * @author wuyiccc
 * @date 2024/11/21 19:34
 */
public class DefaultSelectStrategy implements SelectStrategy {

    public static final SelectStrategy INSTANCE = new DefaultSelectStrategy();

    private DefaultSelectStrategy() {

    }

    @Override
    public int calculateStrategy(IntSupplier selectSupplier, boolean hasTasks) throws Exception {

        // 如果有任务, 则返回任务数量, 否则返回SELECT操作
        return hasTasks ? selectSupplier.get() : SelectStrategy.SELECT;
    }
}
