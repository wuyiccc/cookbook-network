package com.wuyiccc.cookbook.network.hellonetty.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author wuyiccc
 * @date 2024/12/7 17:23
 */
public class AbstractConstant<T extends AbstractConstant<T>> implements Constant<T> {


    // 这个long类型的id是用来比较常量大小的
    private static final AtomicLong uniqueIdGenerator = new AtomicLong();


    private final int id;

    private final String name;


    private final long uniquifier;


    protected AbstractConstant(int id, String name) {

        this.id = id;
        this.name = name;
        this.uniquifier = uniqueIdGenerator.getAndIncrement();
    }

    @Override
    public String name() {

        return name;
    }

    @Override
    public int id() {

        return id;
    }


    @Override
    public String toString() {

        return name();
    }

    @Override
    public int hashCode() {

        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int compareTo(T o) {

        if (this == o) {
            return 0;
        }

        AbstractConstant<T> other = o;

        int returnCode;

        returnCode = hashCode() - other.hashCode();

        if (returnCode != 0) {
            return returnCode;
        }

        if (uniquifier < other.uniquifier) {

            return -1;
        }

        if (uniquifier > other.uniquifier) {

            return 1;
        }

        throw new Error("failed to compare twp different constants");
    }
}
