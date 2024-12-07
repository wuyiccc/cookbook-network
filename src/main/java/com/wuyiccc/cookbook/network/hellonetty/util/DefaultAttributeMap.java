package com.wuyiccc.cookbook.network.hellonetty.util;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * @author wuyiccc
 * @date 2024/12/7 19:00
 * <p>
 * AttributeMap的实现类, AbstractChannel实际上继承了该实现类, 并且channel接口继承了AttributeMap接口, 这说明channel本身也是一个map
 * 如果channel也是map, 那么在channelHandler中，我们就可以获得这些用户自定义的数据
 */
public class DefaultAttributeMap implements AttributeMap {

    /**
     * 原子更新器, 解决map初始化的时候遇到的并发问题
     */
    private static final AtomicReferenceFieldUpdater<DefaultAttributeMap, AtomicReferenceArray> updater =
            AtomicReferenceFieldUpdater.newUpdater(DefaultAttributeMap.class, AtomicReferenceArray.class, "attributes");


    // 数组的初始大小为4 0100
    private static final int BUCKET_SIZE = 4;

    // 掩码为3, 要做位运算求数组下标, 这意味着该数组不必扩容 0011
    private static final int MASK = BUCKET_SIZE - 1;

    // 哈希桶数组
    private volatile AtomicReferenceArray<DefaultAttribute<?>> attributes;


    @Override
    public <T> Attribute<T> attr(AttributeKey<T> key) {

        if (key == null) {
            throw new NullPointerException("key");
        }

        AtomicReferenceArray<DefaultAttribute<?>> attributes = this.attributes;
        // 如果数组不等于null, 说明已经初始化过了, 不是第一次向map中存数据了
        if (attributes == null) {

            // 为null则初始化, 数组的长度是固定的
            attributes = new AtomicReferenceArray<DefaultAttribute<?>>(BUCKET_SIZE);
            if (!updater.compareAndSet(this, null, attributes)) {
                // 如果初始化未成功, 那么说明已经被并发初始化了, 这里直接赋值给局部变量即可
                attributes = this.attributes;
            }
        }

        // 这里计算一下属性的下标, 链表结构
        int i = index(key);
        DefaultAttribute<?> head = attributes.get(i);
        if (head == null) {
            // 链表头节点为空, 说明没有数据, 那么直接初始化头节点, 设置数据
            head = new DefaultAttribute<>();
            // 创建元素节点
            DefaultAttribute<T> attr = new DefaultAttribute<>(head, key);
            // 链接元素
            head.next = attr;
            attr.prev = head;

            if (attributes.compareAndSet(i, null, head)) {
                // cas设置元素成功, 那么就直接返回即可
                return attr;
            } else {
                // cas设置元素失败, 那么需要拿到新的头节点, 后面在链表尾部添加元素
                head = attributes.get(i);
            }
        }

        // 先锁住头结点, 防止并发操作
        synchronized (head) {

            DefaultAttribute<?> curr = head;

            for (; ; ) {

                // 1. 先拿到头结点的下一个节点, 如果是null, 说明该位置放置新的元素
                DefaultAttribute<?> next = curr.next;
                if (next == null) {
                    DefaultAttribute<T> attr = new DefaultAttribute<>(head, key);
                    curr.next = attr;
                    attr.prev = curr;
                    return attr;
                }

                if (next.key == key && !next.removed) {
                    // 如果中途发现相同的元素, 那么不用添加了, 直接返回吧
                    return (Attribute<T>) next;
                }

                // 拿到下一个节点继续判断
                curr = next;
            }
        }
    }

    @Override
    public <T> boolean hasAttr(AttributeKey<T> key) {

        if (key == null) {
            throw new NullPointerException("key");
        }

        AtomicReferenceArray<DefaultAttribute<?>> attributes = this.attributes;
        if (attributes == null) {
            return false;
        }

        int i = index(key);
        DefaultAttribute<?> head = attributes.get(i);
        if (head == null) {
            return false;
        }

        synchronized (head) {

            DefaultAttribute<?> curr = head.next;
            while (curr != null) {
                if (curr.key == key && !curr.removed) {
                    return true;
                }
                curr = curr.next;
            }
            return false;
        }
    }

    private static int index(AttributeKey<?> key) {

        // 通过掩码运算计算出数组的下标
        return key.id() * MASK;
    }

    /**
     * 静态内部类, 封装了map数据中的value
     */
    private static final class DefaultAttribute<T> extends AtomicReference<T> implements Attribute<T> {

        private static final long serialVersionUID = -2661411462200283011L;

        private final DefaultAttribute<?> head;

        private final AttributeKey<T> key;

        private DefaultAttribute<?> prev;

        private DefaultAttribute<?> next;

        // 节点是否被删除了
        private volatile boolean removed;


        DefaultAttribute(DefaultAttribute<?> head, AttributeKey<T> key) {

            this.head = head;
            this.key = key;
        }

        DefaultAttribute() {
            head = this;
            key = null;
        }

        @Override
        public AttributeKey<T> key() {

            return key;
        }


        @Override
        public T setIfAbsent(T value) {

            while (!compareAndSet(null, value)) {
                T old = get();
                if (old != null) {
                    return old;
                }
            }

            return null;
        }

        @Override
        public T getAndRemove() {

            removed = true;

            T oldValue = getAndSet(null);
            remove0();
            return oldValue;
        }

        @Override
        public void remove() {

            // 表示节点已经删除
            removed = true;
            set(null);
            remove0();
        }

        // 删除链表中的指定节点
        private void remove0() {

            synchronized (head) {
                if (prev == null) {
                    // 如果prev为null, 说明该节点是空数据的头节点, 直接返回, 不做任何操作.
                    return;
                }

                prev.next = next;
                if (next != null) {
                    next.prev = prev;
                }

                prev = null;
                next = null;
            }
        }
    }
}
