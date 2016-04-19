package com.foo.p1;

import java.util.Arrays;

/**
 * @author ding.lid
 */
public class P1C1 {
    static {
        System.out.printf("loaded class %s by class loader %s.%n", P1C1.class.getName(), P1C1.class.getClassLoader());
    }

    private P1C1() {}

    public static void main(String[] args) {
        System.out.println("args is: " + Arrays.toString(args));
        P1C1 foo = new P1C1();
        System.out.println("new " + foo.toString());
    }
}
