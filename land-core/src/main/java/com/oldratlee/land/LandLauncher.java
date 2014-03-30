package com.oldratlee.land;

import java.net.URL;
import java.util.HashMap;
import java.util.List;

/**
 * @author ding.lid
 */
public class LandLauncher {
    private static final LandLauncher launcher = new LandLauncher();

    public static LandLauncher getLauncher() {
        return launcher;
    }

    public ClassLoader createClassLoader(final URL[] urls) {
        return new LandClassLoader(new HashMap<LandClassLoader.DelegateType, List<String>>(), urls);
    }
}
