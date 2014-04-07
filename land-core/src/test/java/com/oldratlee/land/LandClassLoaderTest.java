package com.oldratlee.land;

import com.oldratlee.land.matcher.DefaultLandMatcher;
import com.oldratlee.land.matcher.LandMatcher;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import static com.oldratlee.land.DelegateType.CHILD_ONLY;
import static com.oldratlee.land.DelegateType.PARENT_CHILD;
import static com.oldratlee.land.DelegateType.PARENT_ONLY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author ding.lid
 */
public class LandClassLoaderTest {
    LandMatcher matcher = new DefaultLandMatcher();

    @Test
    public void testMatch_list() throws Exception {
        List<String> list = Arrays.asList("foo.p1.A", "foo.p2.*", "foo.p3.**");

        assertFalse(LandClassLoader.matchPatterns("foo.A", list, matcher));

        assertTrue(LandClassLoader.matchPatterns("foo.p1.A", list, matcher));
        assertFalse(LandClassLoader.matchPatterns("foo.p1.B", list, matcher));
        assertFalse(LandClassLoader.matchPatterns("foo.p1.p.A", list, matcher));

        assertTrue(LandClassLoader.matchPatterns("foo.p2.A", list, matcher));
        assertFalse(LandClassLoader.matchPatterns("foo.p2.p.A", list, matcher));

        assertTrue(LandClassLoader.matchPatterns("foo.p3.A", list, matcher));
        assertTrue(LandClassLoader.matchPatterns("foo.p3.p.A", list, matcher));
    }

    @Test
    public void testFindDelegateType() throws Exception {
        Map<DelegateType, List<String>> delegateConfig = new LinkedHashMap<>();
        delegateConfig.put(PARENT_ONLY, Arrays.asList(
                "foo.p2.*",
                "foo.p3.**"));
        delegateConfig.put(CHILD_ONLY, Arrays.asList(
                "foo.p2.p.*",
                "foo.p2.q.**",
                "foo.p3.**",
                "foo.p4.*"));

        assertEquals(PARENT_CHILD, LandClassLoader.findDelegateTypeInDelegateConfigs("A", delegateConfig, matcher));


        assertEquals(PARENT_ONLY, LandClassLoader.findDelegateTypeInDelegateConfigs("foo.p2.A", delegateConfig, matcher));

        assertEquals(CHILD_ONLY, LandClassLoader.findDelegateTypeInDelegateConfigs("foo.p2.p.A", delegateConfig, matcher));
        assertEquals(PARENT_CHILD, LandClassLoader.findDelegateTypeInDelegateConfigs("foo.p2.p.pp.A", delegateConfig, matcher));

        assertEquals(CHILD_ONLY, LandClassLoader.findDelegateTypeInDelegateConfigs("foo.p2.q.A", delegateConfig, matcher));
        assertEquals(CHILD_ONLY, LandClassLoader.findDelegateTypeInDelegateConfigs("foo.p2.q.qq.A", delegateConfig, matcher));


        assertEquals(PARENT_ONLY, LandClassLoader.findDelegateTypeInDelegateConfigs("foo.p3.A", delegateConfig, matcher));
        assertEquals(PARENT_ONLY, LandClassLoader.findDelegateTypeInDelegateConfigs("foo.p3.p.A", delegateConfig, matcher));


        assertEquals(CHILD_ONLY, LandClassLoader.findDelegateTypeInDelegateConfigs("foo.p4.A", delegateConfig, matcher));
        assertEquals(PARENT_CHILD, LandClassLoader.findDelegateTypeInDelegateConfigs("foo.p4.p.A", delegateConfig, matcher));
    }
}
