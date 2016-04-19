package com.foo.p1.p11;

import java.util.Arrays;

/**
 * @author ding.lid
 */
public class P11C2 {
    static {
        System.out.printf("loaded class %s by class loader %s.%n", P11C2.class.getName(), P11C2.class.getClassLoader());
    }

    private P11C2() {}

    public static void main(String[] args) {
        System.out.println("args is: " + Arrays.toString(args));
        P11C2 foo = new P11C2();
        System.out.println("new " + foo.toString());
    }
}
