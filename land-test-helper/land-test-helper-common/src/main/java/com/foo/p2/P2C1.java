package com.foo.p2;

import java.util.Arrays;

/**
 * @author ding.lid
 */
public class P2C1 {
    static {
        System.out.printf("loaded class %s by class loader %s.%n", P2C1.class.getName(), P2C1.class.getClassLoader());
    }

    public static void main(String[] args) {
        System.out.println("args is: " + Arrays.toString(args));
        P2C1 foo = new P2C1();
        System.out.println("new " + foo.toString());
    }
}
