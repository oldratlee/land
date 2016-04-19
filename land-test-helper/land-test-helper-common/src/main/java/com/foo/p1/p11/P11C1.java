package com.foo.p1.p11;

import java.util.Arrays;

/**
 * @author ding.lid
 */
public class P11C1 {
    static {
        System.out.printf("loaded class %s by class loader %s.%n", P11C1.class.getName(), P11C1.class.getClassLoader());
    }

    public static void main(String[] args) {
        System.out.println("args is: " + Arrays.toString(args));
        P11C1 foo = new P11C1();
        System.out.println("new " + foo.toString());
    }
}
