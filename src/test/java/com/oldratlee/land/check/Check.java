package com.oldratlee.land.check;

import java.io.File;

/**
 * @author ding.lid
 */
public class Check {
    public static void main(String[] args) throws Exception {
        System.out.println("getContextClassLoader: " + Thread.currentThread().getContextClassLoader());

        System.out.println("work directory: " + new File(".").getCanonicalPath());

        System.out.flush();
        Thread.sleep(10);

        throw new RuntimeException("show main call stack, check classloader!");
    }
}
