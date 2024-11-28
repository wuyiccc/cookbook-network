package com.wuyiccc.cookbook.network.day14.other.concurent;

import com.wuyiccc.cookbook.network.day14.core.executor.EventExecutor;
import com.wuyiccc.cookbook.network.day14.other.util.ObjectUtil;
import com.wuyiccc.cookbook.network.day14.other.util.StringUtil;

import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * @author wuyiccc
 * @date 2024/11/26 13:44
 */
public class DefaultPromise<V> extends AbstractFuture<V> implements Promise<V> {

    // 每一个promise都要有执行器来执行, 对应的执行器要赋值给该属性
    private final EventExecutor executor;

    // 执行后得到的结果要赋值给该属性, 使用AtomicReferenceFieldUpdater进行原子更新必须用volatile修饰
    private volatile Object result;


    // 原子更新器, 更新result的值
    private static final AtomicReferenceFieldUpdater<DefaultPromise, Object> RESULT_UPDATER = AtomicReferenceFieldUpdater.newUpdater(DefaultPromise.class, Object.class, "result");


    // 该属性是为了给result赋值, 前提是promise的返回类型为void,
    private static final Object SUCCESS = new Object();

    // 当该任务不可取消的时候, 则原子更新器使用该值更新结果
    private static final Object UNCANCELLABLE = new Object();

    // 需要通知的监听器
    private Object listeners;

    // 一定会出现这样的情况, 当外部线程调用该类的get方法的时候, 如果任务还未执行完毕, 则外部线程将视情况阻塞，每当一个外部线程阻塞时，该属性便加一，线程继续执行后，该属性减一
    // 排队等待的线程数
    private short waiters;

    // 防止并发通知的情况出现, 如果为true, 则说明有线程通知监听器了，为false则说明没有
    private boolean notifyingListeners;

    public DefaultPromise(EventExecutor executor) {

        this.executor = ObjectUtil.checkNotNull(executor, "executor");
    }

    protected DefaultPromise() {

        this.executor = null;
    }

    // 得到传入的执行器
    protected EventExecutor executor() {

        return this.executor;
    }

    // promise和future的区别就是, promise可以让用户自己设置成功的返回值, 也可以设置失败后的错误
    @Override
    public Promise<V> setSuccess(V result) {

        if (setSuccess0(result)) {
            return this;
        }

        throw new IllegalStateException("complete already: " + this);
    }


    // 与setSuccess功能相同, 但是这个方法在设置失败的时候不会抛出异常, 而是直接返回false
    @Override
    public boolean trySuccess(V result) {

        return setSuccess0(result);
    }

    @Override
    public Promise<V> setFailure(Throwable cause) {

        if (setFailure0(cause)) {
            return this;
        }

        throw new IllegalStateException("complete already: " + this, cause);
    }


    @Override
    public boolean tryFailure(Throwable cause) {

        return setFailure0(cause);
    }

    // 设置当前任务为不可取消
    @Override
    public boolean setUncancellable() {

        // 用原子更新器更新result的值
        if (RESULT_UPDATER.compareAndSet(this, null, UNCANCELLABLE)) {
            return true;
        }

        // 1. 任务已经执行完毕
        // 2. 任务已经被其他线程设置为不可取消
        Object result = this.result;


        // 如果任务已经执行完成, 那么根据是否取消进行判断
        // 如果任务未执行完成, 那么直接返回true
        return !isDone0(result) || !isCancelled0(result);
    }

    @Override
    public boolean isSuccess() {

        Object result = this.result;
        return result != null && result != UNCANCELLABLE && !(result instanceof CauseHolder);
    }

    @Override
    public boolean isCancellable() {

        return result == null;
    }

    @Override
    public Throwable cause() {

        Object result = this.result;
        // 如果得到的结果属于包装过的异常类, 说明任务执行时是有异常的, 直接从包装过的类中得到异常属性即可, 如果不属于包装过的异常类, 则直接返回null即可
        return (result instanceof CauseHolder) ? ((CauseHolder) result).cause : null;
    }

    @Override
    public Promise<V> addListener(GenericFutureListener<? extends Future<? super V>> listener) {

        // 检查监听器不为null
        ObjectUtil.checkNotNull(listener, "listener");
        // 加锁
        synchronized (this) {
            // 添加监听器
            addListener0(listener);
        }

        // 判断任务是否完成, 实际上就是检查result是否被赋值了
        if (isDone()) {
            // 唤醒监听器, 让监听器去执行
            notifyListeners();
        }

        // 最后返回当前对象
        return this;
    }


    @Override
    public Promise<V> addListeners(GenericFutureListener<? extends Future<? super V>>... listeners) {

        ObjectUtil.checkNotNull(listeners, "listeners");

        synchronized (this) {

            // 遍历传入的监听器, 如果其中任何一个为null, 则停止循环
            for (GenericFutureListener<? extends Future<? super V>> listener : listeners) {
                if (listener == null) {
                    break;
                }
                addListener0(listener);
            }
        }

        if (isDone()) {
            notifyListeners();
        }

        return this;
    }


    @Override
    public Promise<V> removeListener(GenericFutureListener<? extends Future<? super V>> listener) {

        ObjectUtil.checkNotNull(listener, "listener");

        synchronized (this) {
            removeListener0(listener);
        }

        return this;
    }


    @Override
    public Promise<V> removeListeners(GenericFutureListener<? extends Future<? super V>>... listeners) {

        ObjectUtil.checkNotNull(listeners, "listeners");

        synchronized (this) {
            for (GenericFutureListener<? extends Future<? super V>> listener : listeners) {
                if (listener == null) {
                    break;
                }
                removeListener0(listener);
            }
        }

        return this;
    }


    @Override
    public Promise<V> await() throws InterruptedException {

        // 如果已经执行完成, 直接返回即可
        if (isDone()) {
            return this;
        }

        // 如果线程中断, 直接抛出异常
        if (Thread.interrupted()) {
            throw new InterruptedException(toString());
        }


        // 检查是否死锁, 如果是死锁直接抛出异常
        // 如果熟悉了netty之后, 就会发现, 凡事结果都要赋值到promise的任务都是由netty中的单线程执行器来执行的
        // 执行每个任务的执行器和channel是绑定的, 如果某个执行器正在执行任务, 但是还未获得结果, 这时候该执行器又来获取结果
        // 一个线程怎么可能同时执行任务又要唤醒自己呢, 所以必然会产生死锁
        checkDeadLock();
        // wait要和synchronized一起使用, 在futureTask的源码中, 这里使用了LockSupport.park方法
        synchronized (this) {
            // 如果成功直接返回, 不成功进入循环
            while (!isDone()) {
                // waiters字段加1，记录在此阻塞的线程数量
                incWaiters();

                try {

                    // 释放锁并等待
                    wait();
                } finally {
                    // 等待结束waiters字段减一
                    decWaiters();
                }

            }
        }
        return this;
    }

    @Override
    public Promise<V> awaitUninterruptibly() {

        if (isDone()) {
            return this;
        }

        checkDeadLock();

        boolean interrupted = false;
        synchronized (this) {
            while (!isDone()) {
                incWaiters();
                try {
                    wait();
                } catch (InterruptedException e) {
                    interrupted = true;
                } finally {
                    decWaiters();
                }
            }
        }

        if (interrupted) {
            Thread.currentThread().interrupt();
        }
        return this;
    }

    @Override
    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {

        return await0(unit.toNanos(timeout), true);
    }

    @Override
    public boolean awaitUninterruptibly(long timeout, TimeUnit unit) {

        try {
            return await0(unit.toNanos(timeout), false);
        } catch (InterruptedException e) {
            throw new InternalError();
        }
    }

    @Override
    public boolean awaitUninterruptibly(long timeoutMillis) {

        try {
            return await0(TimeUnit.MILLISECONDS.toNanos(timeoutMillis), false);
        } catch (InterruptedException e) {
            throw new InternalError();
        }
    }


    @Override
    public V getNow() {

        Object result = this.result;
        if (result instanceof CauseHolder || result == SUCCESS || result == UNCANCELLABLE) {
            return null;
        }

        return (V) result;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {

        // 原子更新器得到当前result的值, 如果为null, 说明任务还未完成, 并且没有被取消
        if (RESULT_UPDATER.get(this) == null
                // 原子更新器把被包装过的CancellationException赋值给result
                && RESULT_UPDATER.compareAndSet(this, null, new CauseHolder(new CancellationException()))) {
            // 如果上面的操作成功了就唤醒之前wait的线程
            if (checkNotifyWaiters()) {
                // 通知所有监听器执行
                notifyListeners();
            }
            return true;
        }
        // 如果取消失败, 则说明result已经有值了
        return false;
    }


    @Override
    public boolean isCancelled() {

        return isCancelled0(result);
    }

    @Override
    public boolean isDone() {
        return isDone0(result);
    }


    @Override
    public Promise<V> sync() throws InterruptedException {

        await();
        rethrowIfFailed();
        return this;
    }


    @Override
    public Promise<V> syncUninterruptibly() {

        awaitUninterruptibly();
        rethrowIfFailed();
        return this;
    }

    private static final class CauseHolder {

        final Throwable cause;

        CauseHolder(Throwable cause) {
            this.cause = cause;
        }
    }

    @Override
    public String toString() {
        return toStringBuilder().toString();
    }

    private boolean setSuccess0(V result) {

        // 设置成功结果, 如果结果为null, 则将SUCCESS赋值
        return setValue0(result == null ? SUCCESS : result);
    }

    private boolean setValue0(Object objResult) {

        // result还未被赋值的时候, 原子更新器可以将结果赋值给result
        if (RESULT_UPDATER.compareAndSet(this, null, objResult) ||
                RESULT_UPDATER.compareAndSet(this, UNCANCELLABLE, objResult)) {

            // 如果有在获得结果时被阻塞的线程, 则唤醒这些线程
            if (checkNotifyWaiters()) {
                // 得到结果之后就执行监听器的回调方法
                notifyListeners();
            }
            return true;
        }
        return false;
    }

    private synchronized boolean checkNotifyWaiters() {

        if (waiters > 0) {
            notifyAll();
        }
        return listeners != null;
    }


    private void notifyListeners() {

        // 得到执行器
        EventExecutor executor = executor();
        // 如果正在执行方法的线程就是执行器的线程, 就立刻通知监听器执行方法
        if (executor.inEventLoop(Thread.currentThread())) {
            notifyListenersNow();
        }

        safeExecute(executor, new Runnable() {
            @Override
            public void run() {
                notifyListenersNow();
            }
        });

    }

    protected StringBuilder toStringBuilder() {

        StringBuilder buf = new StringBuilder(64)
                .append(StringUtil.simpleClassName(this))
                .append("@")
                .append(Integer.toHexString(hashCode()));

        Object result = this.result;
        if (result == SUCCESS) {
            buf.append("(success)");
        } else if (result == UNCANCELLABLE) {
            buf.append("(uncancellable)");
        } else if (result instanceof CauseHolder) {
            buf.append("(failure: ")
                    .append(((CauseHolder) result).cause)
                    .append(')');
        } else if (result != null) {
            buf.append("(success: ")
                    .append(result)
                    .append(')');
        } else {
            buf.append("(incomplete)");

        }

        return buf;
    }

    // 检查是否死锁
    protected void checkDeadLock() {

        // 得到执行器
        EventExecutor e = executor();
        // 判断是否为死锁
        if (e != null && e.inEventLoop(Thread.currentThread())) {
            throw new BlockingOperationException(toString());
        }
    }

    protected static void notifyListener(EventExecutor eventExecutor, final Future<?> future, final GenericFutureListener<?> listener) {

        ObjectUtil.checkNotNull(eventExecutor, "eventExecutor");
        ObjectUtil.checkNotNull(future, "future");
        ObjectUtil.checkNotNull(listener, "listener");

        if (eventExecutor.inEventLoop(Thread.currentThread())) {
            // 如果执行任务的线程是单线程执行器, 那么直接通知监听器执行方法
            notifyListener0(future, listener);
        }

        safeExecute(eventExecutor, new Runnable() {
            @Override
            public void run() {
                notifyListener0(future, listener);
            }
        });
    }


    private void notifyListenersNow() {

        Object listeners;

        synchronized (this) {
            // notifyingListeners这个属性如果为true, 说明已经有线程通知监听器了, 或者当监听器属性为null
            // 这个时候直接返回即可
            if (notifyingListeners || this.listeners == null) {
                return;
            }

            // 如果没有通知, 把notifyingListeners设置为true
            notifyingListeners = true;
            listeners = this.listeners;
            // 将listeners属性设置为null, 代表已经通知过了, 这个时候锁就要被释放, 当有其他线程进入该代码块的时候, 就不会进入if判断, 而是直接进入for循环
            this.listeners = null;
        }

        for (; ; ) {

            if (listeners instanceof DefaultFutureListeners) {

                notifyListeners0((DefaultFutureListeners) listeners);
            } else {
                // 说明只有一个监听器
                notifyListener0(this, (GenericFutureListener<?>) listeners);
            }

            // 通知完成之后继续上锁
            synchronized (this) {
                // 这次再次加锁是因为方法结束之后notifyListeners的值要重置
                if (this.listeners == null) {
                    notifyingListeners = false;
                    // 重置之后退出即可
                    return;
                }
                // 如果走到这里就说明将要重置notifyListeners之前, 又添加了监听器, 这时候要重复上一个synchronized代码块中的内容
                // 为下一次循环做准备, 而在循环的时候也有可能
                listeners = this.listeners;
                this.listeners = null;
            }
        }


    }

    private void notifyListeners0(DefaultFutureListeners listeners) {

        // 得到监听器数组
        GenericFutureListener<?>[] a = listeners.listeners();
        // 遍历数组, 一次性通知监听器执行方法
        int size = listeners.size();
        for (int i = 0; i < size; i++) {
            notifyListener0(this, a[i]);
        }
    }

    private static void notifyListener0(Future future, GenericFutureListener l) {

        try {
            l.operationComplete(future);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }


    private void addListener0(GenericFutureListener<? extends Future<? super V>> listener) {

        // listeners为null, 则说明在这之前没有添加监听器, 直接把该监听器赋值给属性即可
        if (listeners == null) {
            listeners = listener;
        } else if (listeners instanceof DefaultFutureListeners) {

            ((DefaultFutureListeners) listeners).add(listener);
        } else {
            // 这种情况适用于第二次添加的时候, 把第一次添加的监听器和本次添加的监听器传入DefaultFutureListeners的构造器函数中
            // 封装为一个监听器数组
            listeners = new DefaultFutureListeners((GenericFutureListener<?>) listeners, listener);
        }
    }

    // 删除监听器
    private void removeListener0(GenericFutureListener<? extends Future<? super V>> listener) {

        // 如果监听器是数组类型的, 就从数组中删除
        if (listener instanceof DefaultFutureListeners) {
            ((DefaultFutureListeners) listeners).remove(listener);
        } else if (listeners == listener) {
            // 如果只有一个监听器, 则直接把监听器属性设置为null
            listeners = null;
        }
    }

    private boolean setFailure0(Throwable cause) {
        // 设置失败结果, 也就是包装过的异常信息
        return setValue0(new CauseHolder(ObjectUtil.checkNotNull(cause, "cause")));
    }


    private void incWaiters() {
        if (waiters == Short.MAX_VALUE) {
            throw new IllegalStateException("too many waiters: " + this);
        }
        ++waiters;
    }

    private void decWaiters() {
        --waiters;
    }

    private void rethrowIfFailed() {

        Throwable cause = cause();

        if (cause == null) {
            return;
        }

        // 暂时先不从源码中引入该工具类
        //PlatformDependent.throwException(cause);
    }

    private boolean await0(long timeoutNanos, boolean interruptable) throws InterruptedException {

        // 执行成功则直接返回
        if (isDone()) {
            return true;
        }

        // 传入的时间小于0则直接判断是否执行完成
        if (timeoutNanos <= 0) {
            return isDone();
        }


        // interruptable为true则允许抛出中断异常, 为false则不允许, 判断当前线程是否被中断了
        // 如果都为true则抛出中断异常
        if (interruptable && Thread.interrupted()) {
            throw new InterruptedException(toString());
        }

        // 检查死锁
        checkDeadLock();

        // 获取当前纳秒时间
        long startTime = System.nanoTime();
        // 用户设置的等待时间
        long waitTime = timeoutNanos;
        // 是否中断
        boolean interrupted = false;

        try {


            for (; ; ) {
                synchronized (this) {
                    // 再次判断是否执行完成, 防止出现竞争锁的时候, 任务先完成了, 而外部线程还没有开始阻塞的情况
                    if (isDone()) {
                        return true;
                    }

                    // 如果没有执行完成, 则开始阻塞等待, 阻塞线程数加1
                    incWaiters();
                    try {
                        // 阻塞在这里
                        wait(waitTime / 1000000, (int) (waitTime % 1000000));
                    } catch (InterruptedException e) {
                        if (interruptable) {
                            throw e;
                        } else {
                            // 中断标记设置为true
                            interrupted = true;
                        }
                    } finally {

                        // 阻塞线程数减一
                        decWaiters();
                    }
                }
                // 走到这里说明线程被唤醒了
                if (isDone()) {
                    return true;
                } else {
                    // 可能是虚假唤醒
                    // 得到新的等待时间, 如果等待时间小于0, 表示已经阻塞了用户设定的等待时间, 如waitTime大于0, 则继续循环
                    waitTime = timeoutNanos - (System.nanoTime() - startTime);
                    if (waitTime <= 0) {
                        return isDone();
                    }
                }
            }
        } finally {

            // 退出方法判断是否要给执行任务的线程添加中断标记
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }


    }

    private static boolean isCancelled0(Object result) {

        return result instanceof CauseHolder && ((CauseHolder) result).cause instanceof CancellationException;
    }

    private static boolean isDone0(Object result) {

        return result != null && result != UNCANCELLABLE;
    }

    private static void safeExecute(EventExecutor executor, Runnable task) {

        try {
            executor.execute(task);
        } catch (Throwable t) {
            throw new RuntimeException("Failed to submit a listener notification task. Event loop shut down?", t);
        }
    }

}
