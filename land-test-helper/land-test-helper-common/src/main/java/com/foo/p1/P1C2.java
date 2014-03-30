package com.foo.p1;

import java.util.Arrays;

/**
 * @author ding.lid
 */
public class P1C2 {
    static {
        System.out.printf("loaded class %s by class loader %s.\n", P1C2.class.getName(), P1C2.class.getClassLoader());
    }

    public static void main(String[] args) {
        System.out.println("args is: " + Arrays.toString(args));
        P1C2 foo = new P1C2();
        System.out.println("new " + foo.toString());
    }
}
