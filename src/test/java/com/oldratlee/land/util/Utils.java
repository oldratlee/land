package com.oldratlee.land.util;

import java.lang.reflect.Method;

/**
 * @author ding.lid
 */
public class Utils {
    public static void invokeMain(Class<?> fooClass) throws Exception {
        System.out.println();
        System.out.println(fooClass.getName() + " loaded by " + fooClass.getClassLoader());
        
        Method main = fooClass.getMethod("main", String[].class);
        System.out.println("Get method: " + main);
        
        Object ret = main.invoke(null, new Object[]{new String[]{"a", "b", "c"}});
        System.out.printf("Return is %s\n", ret);
    }
}
