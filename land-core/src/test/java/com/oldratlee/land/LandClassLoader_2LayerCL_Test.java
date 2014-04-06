package com.oldratlee.land;

import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import static com.oldratlee.land.Constants.lib_api;
import static com.oldratlee.land.Constants.lib_class_api;
import static com.oldratlee.land.Constants.lib_class_impl;
import static com.oldratlee.land.Constants.lib_common;
import static com.oldratlee.land.Constants.lib_impl;
import static com.oldratlee.land.Constants.lib_package_api;
import static com.oldratlee.land.Constants.lib_package_impl;
import static com.oldratlee.land.DelegateType.CHILD_ONLY;
import static com.oldratlee.land.DelegateType.CHILD_PARENT;
import static com.oldratlee.land.DelegateType.NONE;
import static com.oldratlee.land.DelegateType.PARENT_CHILD;
import static com.oldratlee.land.DelegateType.PARENT_ONLY;
import static com.oldratlee.land.util.Utils.invokeMain;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * @author ding.lid
 */
public class LandClassLoader_2LayerCL_Test {
    @Test
    public void test_ClassInSystemCL_cannot_overwrite() throws Exception {
        Map<DelegateType, List<String>> parentDelegateConfig = new HashMap<>();
        parentDelegateConfig.put(CHILD_ONLY, Arrays.asList("com.oldratlee.land.*"));
        ClassLoader parent = new LandClassLoader(parentDelegateConfig, new URL[]{lib_common});

        assertEquals(ClassLoader.getSystemClassLoader(), parent.loadClass("com.oldratlee.land.DelegateType").getClassLoader());

        Map<DelegateType, List<String>> childDelegateConfig = new HashMap<>();
        childDelegateConfig.put(NONE, Arrays.asList("com.oldratlee.land.*"));
        ClassLoader child = new LandClassLoader(childDelegateConfig, new URL[]{lib_common}, parent);

        assertEquals(ClassLoader.getSystemClassLoader(), child.loadClass("com.oldratlee.land.DelegateType").getClassLoader());
    }

    @Test
    public void test_NONE() throws Exception {
        ClassLoader parent = new LandClassLoader(null, new URL[]{lib_common, lib_api});

        Map<DelegateType, List<String>> delegateConfig = new HashMap<>();
        delegateConfig.put(NONE, Arrays.asList(
                "com.foo.p1.*",
                lib_package_api + ".*",
                lib_package_impl + ".*"));
        ClassLoader child = new LandClassLoader(delegateConfig, new URL[]{lib_common, lib_impl}, parent);

        // In Child, default parent-child
        Class<?> fooClass = child.loadClass("com.foo.Foo");
        assertEquals(parent, fooClass.getClassLoader());
        invokeMain(fooClass);

        // exists in parent and child
        try {
            child.loadClass("com.foo.p1.P1C1");
            fail();
        } catch (ClassNotFoundException expected) {
            assertThat(expected.getMessage(), containsString("(NONE) is forbidden by land config")); // TODO improve message
            expected.printStackTrace(System.out);
        }

        // exists in parent
        try {
            child.loadClass(lib_class_api);
            fail();
        } catch (ClassNotFoundException expected) {
            assertThat(expected.getMessage(), containsString("(NONE) is forbidden by land config")); // TODO improve message
            expected.printStackTrace(System.out);
        }

        // exists in child
        try {
            child.loadClass(lib_class_impl);
            fail();
        } catch (ClassNotFoundException expected) {
            assertThat(expected.getMessage(), containsString("(NONE) is forbidden by land config")); // TODO improve message
            expected.printStackTrace(System.out);
        }
    }

    @Test
    public void test_PARENT_ONLY() throws Exception {
        ClassLoader parent = new LandClassLoader(null, new URL[]{lib_common, lib_api});

        Map<DelegateType, List<String>> delegateConfig = new HashMap<>();
        delegateConfig.put(PARENT_ONLY, Arrays.asList(
                "com.foo.p1.*",
                lib_package_api + ".*",
                lib_package_impl + ".*"));
        ClassLoader child = new LandClassLoader(delegateConfig, new URL[]{lib_common, lib_impl}, parent);

        // In Child, default parent-child
        Class<?> fooClass = child.loadClass("com.foo.Foo");
        assertEquals(parent, fooClass.getClassLoader());
        invokeMain(fooClass);

        // parent-only
        Class<?> aClass = child.loadClass("com.foo.p1.P1C1");
        assertEquals(parent, aClass.getClassLoader());
        invokeMain(aClass);

        // only in parent, ok
        Class<?> apiClass = child.loadClass(lib_class_api);
        assertEquals(parent, apiClass.getClassLoader());
        invokeMain(apiClass);

        // exists in child, but is parent-only scope
        try {
            child.loadClass(lib_class_impl);
            fail();
        } catch (ClassNotFoundException expected) {
            // assertThat(expected.getMessage(), containsString("PARENT_ONLY")); // TODO improve message
            expected.printStackTrace(System.out);
        }
    }

    @Test
    public void test_CHILD_ONLY() throws Exception {
        ClassLoader parent = new LandClassLoader(null, new URL[]{lib_common, lib_api});

        Map<DelegateType, List<String>> delegateConfig = new HashMap<>();
        delegateConfig.put(CHILD_ONLY, Arrays.asList(
                "com.foo.p1.*",
                lib_package_api + ".*",
                lib_package_impl + ".*"));
        ClassLoader child = new LandClassLoader(delegateConfig, new URL[]{lib_common, lib_impl}, parent);

        // In Child, default parent-child
        Class<?> fooClass = child.loadClass("com.foo.Foo");
        assertEquals(parent, fooClass.getClassLoader());
        invokeMain(fooClass);

        // child-only, so use child class loader, although also in parent 
        Class<?> aClass = child.loadClass("com.foo.p1.P1C1");
        assertEquals(child, aClass.getClassLoader());
        invokeMain(aClass);

        // child-only, class only exists in parent
        try {
            child.loadClass(lib_class_api);
            fail();
        } catch (ClassNotFoundException expected) {
            // assertThat(expected.getMessage(), containsString("PARENT_ONLY")); // TODO improve message
            expected.printStackTrace(System.out);
        }

        // exists in child
        Class<?> implClass = child.loadClass(lib_class_impl);
        assertEquals(child, implClass.getClassLoader());
        invokeMain(implClass);
    }

    @Test
    public void test_PARENT_CHILD() throws Exception {
        ClassLoader parent = new LandClassLoader(null, new URL[]{lib_common, lib_api});

        Map<DelegateType, List<String>> delegateConfig = new HashMap<>();
        delegateConfig.put(PARENT_CHILD, Arrays.asList(
                "com.foo.p1.*",
                lib_package_api + ".*",
                lib_package_impl + ".*"));
        ClassLoader child = new LandClassLoader(delegateConfig, new URL[]{lib_common, lib_impl}, parent);

        // In Child, default parent-child
        Class<?> fooClass = child.loadClass("com.foo.Foo");
        assertEquals(parent, fooClass.getClassLoader());
        invokeMain(fooClass);

        Class<?> aClass = child.loadClass("com.foo.p1.P1C1");
        assertEquals(parent, aClass.getClassLoader());
        invokeMain(aClass);

        Class<?> apiClass = child.loadClass(lib_class_api);
        assertEquals(parent, apiClass.getClassLoader());
        invokeMain(apiClass);

        // exists in child
        Class<?> implClass = child.loadClass(lib_class_impl);
        assertEquals(child, implClass.getClassLoader());
        invokeMain(implClass);
    }

    @Test
    public void test_CHILD_PARENT() throws Exception {
        ClassLoader parent = new LandClassLoader(null, new URL[]{lib_common, lib_api});

        Map<DelegateType, List<String>> delegateConfig = new HashMap<>();
        delegateConfig.put(CHILD_PARENT, Arrays.asList(
                "com.foo.p1.*",
                lib_package_api + ".*",
                lib_package_impl + ".*"));
        ClassLoader child = new LandClassLoader(delegateConfig, new URL[]{lib_common, lib_impl}, parent);

        // In Child, default parent-child
        Class<?> fooClass = child.loadClass("com.foo.Foo");
        assertEquals(parent, fooClass.getClassLoader());
        invokeMain(fooClass);

        Class<?> aClass = child.loadClass("com.foo.p1.P1C1");
        assertEquals(child, aClass.getClassLoader());
        invokeMain(aClass);

        Class<?> apiClass = child.loadClass(lib_class_api);
        assertEquals(parent, apiClass.getClassLoader());
        invokeMain(apiClass);

        // exists in child
        Class<?> implClass = child.loadClass(lib_class_impl);
        assertEquals(child, implClass.getClassLoader());
        invokeMain(implClass);

        System.out.println();
        System.out.println("====================================");
        System.out.println(parent.getResource("test-common.properties"));
        System.out.println("====================================");
        Enumeration<URL> resources = parent.getResources("test-common.properties");
        while (resources.hasMoreElements()) {
            System.out.println(resources.nextElement());
        }
        System.out.println();
        System.out.println("====================================");
        System.out.println(child.getResource("test-common.properties"));
        System.out.println("====================================");
        resources = child.getResources("test-common.properties");
        while (resources.hasMoreElements()) {
            System.out.println(resources.nextElement());
        }
    }
}
