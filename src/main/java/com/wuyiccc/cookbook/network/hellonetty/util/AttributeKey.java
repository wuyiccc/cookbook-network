package com.wuyiccc.cookbook.network.hellonetty.util;

/**
 * @author wuyiccc
 * @date 2024/12/7 18:37
 */
public final class AttributeKey<T> extends AbstractConstant<AttributeKey<T>> {

    private static final ConstantPool<AttributeKey<Object>> pool = new ConstantPool<AttributeKey<Object>>() {
        @Override
        protected AttributeKey<Object> newConstant(int id, String name) {

            return new AttributeKey<>(id, name);
        }
    };

    public static <T> AttributeKey<T> valueOf(String name) {

        return (AttributeKey<T>) pool.valueOf(name);
    }

    public static boolean exists(String name) {

        return pool.exists(name);
    }

    private AttributeKey(int id, String name) {

        super(id, name);
    }

}
