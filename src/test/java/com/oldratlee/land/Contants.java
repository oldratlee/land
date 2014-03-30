package com.oldratlee.land;

import java.io.File;
import java.net.URL;

/**
 * @author ding.lid
 */
public class Contants {
    static final URL lib_common;
    static final URL lib_api;
    static final URL lib_impl;

    static final String lib_package_api = "com.bar.api";
    static final String lib_class_api = "com.bar.api.ApiC0";
    static final String lib_package_impl = "com.bar.impl";
    static final String lib_class_impl = "com.bar.impl.ImplC0";

    static {
        try {
            lib_common = new File("ut-jars/land-test-common-0.0.1-SNAPSHOT.jar").toURI().toURL();
            lib_api = new File("ut-jars/land-test-api-0.0.1-SNAPSHOT.jar").toURI().toURL();
            lib_impl = new File("ut-jars/land-test-impl-0.0.1-SNAPSHOT.jar").toURI().toURL();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
