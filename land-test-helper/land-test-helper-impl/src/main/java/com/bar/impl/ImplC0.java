package com.bar.impl;

import java.util.Arrays;

/**
 * @author ding.lid
 */
public class ImplC0 {
    static {
        System.out.printf("loaded class %s by class loader %s.%n", ImplC0.class.getName(), ImplC0.class.getClassLoader());
    }

    public static void main(String[] args) {
        System.out.println("args is: " + Arrays.toString(args));
        ImplC0 foo = new ImplC0();
        System.out.println("new " + foo.toString());
    }
}
