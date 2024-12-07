package com.wuyiccc.cookbook.network.generic.self;

/**
 * @author wuyiccc
 * @date 2024/12/7 16:38
 */
public class Test {

    public static void main(String[] args) {
        B b = new B();
        B b1 = b.get();
        E e = new E();
        B b2 = e.get();
        System.out.println(b2);
    }
}

abstract class A<T extends A<T>> {


    abstract T get();
}


class B extends A<B> {

    @Override
    B get() {
        return null;
    }
}

class C {

}

//class D extends A<C> {
//
//}

class E extends A<B> {

    @Override
    B get() {
        return null;
    }
}