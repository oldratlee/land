package com.foo.p2.p21;

import java.util.Arrays;

/**
 * @author ding.lid
 */
public class P21C1 {
    static {
        System.out.printf("loaded class %s by class loader %s.\n", P21C1.class.getName(), P21C1.class.getClassLoader());
    }

    private P21C1() {}

    public static void main(String[] args) {
        System.out.println("args is: " + Arrays.toString(args));
        P21C1 foo = new P21C1();
        System.out.println("new " + foo.toString());
    }
}
