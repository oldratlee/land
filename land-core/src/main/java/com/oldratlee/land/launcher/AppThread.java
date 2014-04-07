package com.oldratlee.land.launcher;

import java.lang.reflect.Method;

/**
* @author ding.lid
*/
class AppThread extends Thread {
    private final Method mainMethod;
    private final String[] argArray;
    private final String appName;

    public AppThread(Method mainMethod, String[] argArray, String appName) {
        this.mainMethod = mainMethod;
        this.argArray = argArray;
        this.appName = appName;
    }

    @Override
    public void run() {
        try {
            mainMethod.invoke(null, argArray);
        } catch (Exception e) {
            throw new IllegalStateException(String.format(
                    "Exception to run app %s, cause: %s", appName, e.getMessage()), e);
        }
    }
}
