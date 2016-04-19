package com.foo.p2;

import java.util.Arrays;

/**
 * @author ding.lid
 */
public class P2C2 {
    static {
        System.out.printf("loaded class %s by class loader %s.%n", P2C2.class.getName(), P2C2.class.getClassLoader());
    }

    private P2C2() {}

    public static void main(String[] args) {
        System.out.println("args is: " + Arrays.toString(args));
        P2C2 foo = new P2C2();
        System.out.println("new " + foo.toString());
    }
}
