package com.oldratlee.land;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import static com.oldratlee.land.LandClassLoader.DelegateType.CHILD_ONLY;
import static com.oldratlee.land.LandClassLoader.DelegateType.PARENT_CHILD;
import static com.oldratlee.land.LandClassLoader.DelegateType.PARENT_ONLY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author ding.lid
 */
public class LandClassLoaderTest {
    @Test
    public void testMatch_1() throws Exception {
        assertTrue(LandClassLoader.match("foo.A", "foo.A"));

        assertTrue(LandClassLoader.match("foo.A", "foo."));
        assertFalse(LandClassLoader.match("foo.p1.A", "foo."));

        assertTrue(LandClassLoader.match("foo.A", "foo.."));
        assertTrue(LandClassLoader.match("foo.p1.B", "foo.."));
    }

    @Test
    public void testMatch_list() throws Exception {
        List<String> list = Arrays.asList("foo.p1.A", "foo.p2.", "foo.p3..");

        assertFalse(LandClassLoader.match("foo.A", list));

        assertTrue(LandClassLoader.match("foo.p1.A", list));
        assertFalse(LandClassLoader.match("foo.p1.B", list));
        assertFalse(LandClassLoader.match("foo.p1.p.A", list));

        assertTrue(LandClassLoader.match("foo.p2.A", list));
        assertFalse(LandClassLoader.match("foo.p2.p.A", list));

        assertTrue(LandClassLoader.match("foo.p3.A", list));
        assertTrue(LandClassLoader.match("foo.p3.p.A", list));
    }

    @Test
    public void testFindDelegateType() throws Exception {
        Map<LandClassLoader.DelegateType, List<String>> delegateConfig = new LinkedHashMap<>();
        delegateConfig.put(PARENT_ONLY, Arrays.asList(
                "foo.p2.",
                "foo.p3.."));
        delegateConfig.put(CHILD_ONLY, Arrays.asList(
                "foo.p2.p.",
                "foo.p2.q..",
                "foo.p3..",
                "foo.p4."));

        assertEquals(PARENT_CHILD, LandClassLoader.findDelegateType("A", delegateConfig));


        assertEquals(PARENT_ONLY, LandClassLoader.findDelegateType("foo.p2.A", delegateConfig));

        assertEquals(CHILD_ONLY, LandClassLoader.findDelegateType("foo.p2.p.A", delegateConfig));
        assertEquals(PARENT_CHILD, LandClassLoader.findDelegateType("foo.p2.p.pp.A", delegateConfig));

        assertEquals(CHILD_ONLY, LandClassLoader.findDelegateType("foo.p2.q.A", delegateConfig));
        assertEquals(CHILD_ONLY, LandClassLoader.findDelegateType("foo.p2.q.qq.A", delegateConfig));


        assertEquals(PARENT_ONLY, LandClassLoader.findDelegateType("foo.p3.A", delegateConfig));
        assertEquals(PARENT_ONLY, LandClassLoader.findDelegateType("foo.p3.p.A", delegateConfig));


        assertEquals(CHILD_ONLY, LandClassLoader.findDelegateType("foo.p4.A", delegateConfig));
        assertEquals(PARENT_CHILD, LandClassLoader.findDelegateType("foo.p4.p.A", delegateConfig));
    }
}
