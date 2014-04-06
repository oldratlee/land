package com.oldratlee.land.demo;

import com.oldratlee.land.Constants;
import com.oldratlee.land.DelegateType;
import com.oldratlee.land.LandClassLoader;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.oldratlee.land.DelegateType.PARENT_ONLY;
import static com.oldratlee.land.util.Utils.invokeMain;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * @author ding.lid
 */
public class LandClassLoaderDemo {
    public static void main(String[] args) throws Exception {
        Map<DelegateType, List<String>> delegateConfig = new HashMap<>();
        delegateConfig.put(PARENT_ONLY, Arrays.asList("com.foo.p2.", "com.foo.p3.."));

        ClassLoader classLoader = new LandClassLoader(delegateConfig, new URL[]{Constants.lib_common});

        invokeMain(classLoader.loadClass("com.foo.Foo"));
        invokeMain(classLoader.loadClass("com.foo.p1.P1C1"));
        try {
            invokeMain(classLoader.loadClass("com.foo.p2.P2C1"));
            fail();
        } catch (ClassNotFoundException e) {
            assertThat(e.getMessage(), containsString("(PARENT_ONLY) not found in parent class loader"));
        }
    }
}
