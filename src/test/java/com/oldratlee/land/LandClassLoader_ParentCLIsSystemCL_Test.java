package com.oldratlee.land;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import static com.oldratlee.land.Contants.lib_common;
import static com.oldratlee.land.LandClassLoader.DelegateType.NONE;
import static com.oldratlee.land.LandClassLoader.DelegateType.PARENT_ONLY;
import static com.oldratlee.land.util.Utils.invokeMain;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * @author ding.lid
 */
public class LandClassLoader_ParentCLIsSystemCL_Test {
    @BeforeClass
    public static void setUp() throws Exception {
        System.out.println("================================================");
        System.out.println("The classloader of JUnit: " + LandClassLoader_ParentCLIsSystemCL_Test.class.getClassLoader());
        System.out.println("================================================");

        assertEquals(ClassLoader.getSystemClassLoader(), LandClassLoader.class.getClassLoader());
    }

    @Test
    public void test_NONE() throws Exception {
        Map<LandClassLoader.DelegateType, List<String>> delegateConfig = new HashMap<>();
        delegateConfig.put(NONE, Arrays.asList(
                "com.foo.p1.", LandClassLoader_ParentCLIsSystemCL_Test.class.getPackage().getName() + "."));

        ClassLoader classLoader = new LandClassLoader(delegateConfig, new URL[]{lib_common});

        // In NONE scope
        try {
            invokeMain(classLoader.loadClass("com.foo.p1.P1C1"));
            fail();
        } catch (ClassNotFoundException expected) {
            assertThat(expected.getMessage(), containsString("(NONE) is forbidden by land config"));
            expected.printStackTrace(System.out);
        }

        // Not IN NONE scope
        Class<?> fooClass = classLoader.loadClass("com.foo.p2.p21.P21C1");
        assertEquals(classLoader, fooClass.getClassLoader());
        invokeMain(fooClass);

        // IN NONE scope, but in system class loader!
        Class<?> clazz = classLoader.loadClass(LandClassLoader_ParentCLIsSystemCL_Test.class.getName());
        assertEquals(ClassLoader.getSystemClassLoader(), clazz.getClassLoader());

        // class not existed
        try {
            invokeMain(classLoader.loadClass("com.foo.NotExisted"));
            fail();
        } catch (ClassNotFoundException expected) {
            assertEquals(expected.getMessage(), "com.foo.NotExisted");
        }
    }

    @Test
    public void test_ParentOnly() throws Exception {
        Map<LandClassLoader.DelegateType, List<String>> delegateConfig = new HashMap<>();
        delegateConfig.put(PARENT_ONLY, Arrays.asList(
                "com.foo.p1.", LandClassLoader_ParentCLIsSystemCL_Test.class.getPackage().getName() + "."));

        ClassLoader classLoader = new LandClassLoader(delegateConfig, new URL[]{lib_common});

        // In NONE scope
        try {
            invokeMain(classLoader.loadClass("com.foo.p1.P1C1"));
            fail();
        } catch (ClassNotFoundException expected) {
            assertThat(expected.getMessage(), containsString("(PARENT_ONLY) not found in parent class loader"));
            expected.printStackTrace(System.out);
        }

        // Not IN NONE scope
        Class<?> fooClass = classLoader.loadClass("com.foo.p2.p21.P21C1");
        assertEquals(classLoader, fooClass.getClassLoader());
        invokeMain(fooClass);

        // IN NONE scope, but in system class loader!
        Class<?> clazz = classLoader.loadClass(LandClassLoader_ParentCLIsSystemCL_Test.class.getName());
        assertEquals(ClassLoader.getSystemClassLoader(), clazz.getClassLoader());

        // class not existed
        try {
            invokeMain(classLoader.loadClass("com.foo.NotExisted"));
            fail();
        } catch (ClassNotFoundException expected) {
            assertEquals(expected.getMessage(), "com.foo.NotExisted");
        }
    }
}
