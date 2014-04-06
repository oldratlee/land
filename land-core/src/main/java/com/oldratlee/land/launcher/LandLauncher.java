package com.oldratlee.land.launcher;

import com.oldratlee.land.DelegateType;
import com.oldratlee.land.LandClassLoader;

import java.net.URL;
import java.util.HashMap;
import java.util.List;

/**
 * @author ding.lid
 */
public class LandLauncher {
    public static final String LAND_SHARED_LIB_DIR = "land.shared.lib.dir";
    public static final String LAND_APP_DIR = "land.app.dir";
    public static final String LAND_CONF_FILE = "land.conf.file";

    private static final LandLauncher launcher = new LandLauncher();

    public static LandLauncher getLauncher() {
        return launcher;
    }

    public ClassLoader createClassLoader(final URL[] urls) {
        return new LandClassLoader(new HashMap<DelegateType, List<String>>(), urls);
    }

    public static void main(String[] args) {
        
    }
}
