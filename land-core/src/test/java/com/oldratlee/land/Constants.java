package com.oldratlee.land;

import java.io.File;
import java.net.URL;

/**
 * @author ding.lid
 */
public class Constants {
    public static final URL lib_common;
    public static final URL lib_api;
    public static final URL lib_impl;

    public static final String lib_package_api = "com.bar.api";
    public static final String lib_class_api = "com.bar.api.ApiC0";
    public static final String lib_package_impl = "com.bar.impl";
    public static final String lib_class_impl = "com.bar.impl.ImplC0";

    static {
        try {
            lib_common = new File("../land-test-helper/land-test-helper-common/target/land-test-helper-common-0.0.1-SNAPSHOT.jar").getCanonicalFile().toURI().toURL();
            lib_api = new File("../land-test-helper/land-test-helper-api/target/land-test-helper-api-0.0.1-SNAPSHOT.jar").getCanonicalFile().toURI().toURL();
            lib_impl = new File("../land-test-helper/land-test-helper-impl/target/land-test-helper-impl-0.0.1-SNAPSHOT.jar").getCanonicalFile().toURI().toURL();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
