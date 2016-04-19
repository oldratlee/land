package com.foo;

import java.util.Arrays;

/**
 * @author ding.lid
 */
public class Foo {
    static {
        System.out.printf("loaded class %s by class loader %s.%n", Foo.class.getName(), Foo.class.getClassLoader());
    }

    public static void main(String[] args) {
        System.out.println("args is: " + Arrays.toString(args));
        Foo foo = new Foo();
        System.out.println("new " + foo.toString());
    }
}
