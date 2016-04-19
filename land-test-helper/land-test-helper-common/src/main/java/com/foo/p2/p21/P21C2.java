package com.foo.p2.p21;

import java.util.Arrays;

/**
 * @author ding.lid
 */
public class P21C2 {
    static {
        System.out.printf("loaded class %s by class loader %s.\n", P21C2.class.getName(), P21C2.class.getClassLoader());
    }

    private P21C2() {}

    public static void main(String[] args) {
        System.out.println("args is: " + Arrays.toString(args));
        P21C2 foo = new P21C2();
        System.out.println("new " + foo.toString());
    }
}
