package com.bar.api;

import java.util.Arrays;

/**
 * @author ding.lid
 */
public class ApiC0 {
    static {
        System.out.printf("loaded class %s by class loader %s.\n", ApiC0.class.getName(), ApiC0.class.getClassLoader());
    }

    public static void main(String[] args) {
        System.out.println("args is: " + Arrays.toString(args));
        ApiC0 foo = new ApiC0();
        System.out.println("new " + foo.toString());
    }
}
