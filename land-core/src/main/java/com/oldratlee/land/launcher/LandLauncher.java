package com.oldratlee.land.launcher;

import com.oldratlee.land.DelegateType;
import com.oldratlee.land.LandClassLoader;
import com.oldratlee.land.matcher.LandMatcher;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.*;

/**
 * @author ding.lid
 */
public class LandLauncher {
    public static final String LAND_MATCHER_CLASS = "land.matcher.class";
    public static final String LAND_SHARED_DELEGATE_CONFIGS = "land.shared.delegate.configs";

    public static final String LAND_SHARED_LIB_DIR = "land.shared.lib.dir";

    public static final String LAND_APP_NAMES = "land.app.names";

    public static final String LAND_APP_LIB_DIR_PREFIX = "land.app.%s.lib.dir";
    public static final String LAND_APP_DELEGATE_CONFIGS_PREFIX = "land.app.%s.delegate.configs";
    public static final String LAND_APP_MAIN_CLASS_PREFIX = "land.app.%s.main.class";
    public static final String LAND_APP_MAIN_ARGS_PREFIX = "land.app.%s.main.args";

    public static final String MAIN_METHOD_NAME = "main";

    private static final LandLauncher launcher = new LandLauncher();

    public static final Map<String, LandClassLoader> app2LandClassLoaderMap = new HashMap<>();

    public static LandLauncher getLauncher() {
        return launcher;
    }

    public ClassLoader createClassLoader(final URL[] urls) {
        return new LandClassLoader(urls, new EnumMap<DelegateType, List<String>>(DelegateType.class));
    }

    public static void main(String[] args) throws IOException {
        ClassLoader appParentClassLoader = ClassLoader.getSystemClassLoader();
        final LandMatcher matcher = getMatcher();

        if (System.getProperty(LAND_SHARED_LIB_DIR) != null && System.getProperty(LAND_SHARED_LIB_DIR).length() > 0) {
            Map<DelegateType, List<String>> sharedDelegateConfigs = convertDelegateConfigs(System.getProperty(LAND_SHARED_DELEGATE_CONFIGS));
            URL[] sharedLibs = getClassPathFromLibDir(System.getProperty(LAND_SHARED_LIB_DIR));
            LandClassLoader sharedClassLoader = new LandClassLoader(sharedLibs, sharedDelegateConfigs, matcher);
            appParentClassLoader = sharedClassLoader;
        }

        String appNames = System.getProperty(LAND_APP_NAMES);
        String[] appNameArray = appNames.split("\\s*,\\s*");

        for (String appName : appNameArray) {
            Map<DelegateType, List<String>> appDelegateConfigs = convertDelegateConfigs(
                    System.getProperty(String.format(LAND_APP_LIB_DIR_PREFIX, appName)));
            URL[] appLibs = getClassPathFromLibDir(
                    System.getProperty(String.format(LAND_APP_DELEGATE_CONFIGS_PREFIX, appName)));
            LandClassLoader appClassLoader = new LandClassLoader(appLibs, appDelegateConfigs, matcher, appParentClassLoader);
            app2LandClassLoaderMap.put(appName, appClassLoader);
        }

        for (Map.Entry<String, LandClassLoader> entry : app2LandClassLoaderMap.entrySet()) {
            invokeAppMain(entry.getKey(), entry.getValue());
        }
    }

    static LandMatcher getMatcher() {
        String matcherClassName = System.getProperty(LAND_MATCHER_CLASS);
        if (matcherClassName == null || matcherClassName.trim().length() ==0) {
            return null;
        }

        try {
            Class<?> matcherClass = Class.forName(matcherClassName);
            if (!LandMatcher.class.isAssignableFrom(matcherClass)) {
                throw new IllegalStateException("LandMatcher class " + matcherClassName +
                        " is not subclass of " + LandMatcher.class.getName());
            }
            return (LandMatcher) matcherClass.newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Fail to init matcher " + matcherClassName + ", cause: " + e.getMessage(), e);
        }
    }

    static Map<DelegateType, List<String>> convertDelegateConfigs(String delegateConfigs) {
        Map<DelegateType, List<String>> ret = new EnumMap<>(DelegateType.class);

        String[] delegateConfigArray = delegateConfigs.split("\\s*;\\s*");
        for (String delegateConfig : delegateConfigArray) {
            String[] delegateTypeAndPatterns = delegateConfig.trim().split("\\s*=\\s*");
            if (delegateConfigs.length() != 2) {
                throw new IllegalStateException("Wrong delegate type and pattern: " + Arrays.toString(delegateTypeAndPatterns));
            }
            DelegateType delegateType = DelegateType.valueOf(delegateConfigArray[0]);
            String patterns = delegateConfigArray[1];
            ret.put(delegateType, toPatternList(patterns));
        }

        return ret;
    }

    static List<String> toPatternList(String patterns) {
        String[] split = patterns.split("\\s*,\\s*");

        List<String> ret = new ArrayList<>();
        for (String s : split) {
            s = s.trim();
            if (s.length() == 0) continue;
            ret.add(s);
        }
        return ret;
    }

    static URL[] getClassPathFromLibDir(String libPaths) throws IOException {
        List<URL> ret = new ArrayList<>();

        String[] libPathArray = libPaths.split("\\s*:\\s*");
        for (String libPath : libPathArray) {
            File file = new File(libPath);
            if (!file.exists()) {
                continue;
            }
            if (!file.isDirectory() && libPath.endsWith(".jar")) {
                ret.add(file.getCanonicalFile().toURI().toURL());
                continue;
            }
            File[] subFiles = file.listFiles();
            if (null == subFiles) {
                throw new IllegalStateException("Fail to list files from dir " + file);
            }
            for (File subFile : subFiles) {
                if (subFile.isDirectory()) {
                    ret.add(subFile.getCanonicalFile().toURI().toURL());
                } else if (!file.isDirectory() && libPath.endsWith(".jar")) {
                    ret.add(file.getCanonicalFile().toURI().toURL());
                }
            }
        }

        return ret.toArray(new URL[ret.size()]);
    }

    static void invokeAppMain(final String appName, ClassLoader appClassLoader) {
        try {
            String mainClassName = System.getProperty(String.format(LAND_APP_MAIN_CLASS_PREFIX, appName));
            String appArgs = System.getProperty(String.format(LAND_APP_MAIN_ARGS_PREFIX, appName));

            Class<?> mainClass = appClassLoader.loadClass(mainClassName);
            final String[] argArray = appArgs.split("\\s+"); // TODO argument can not contain space! 

            final Method mainMethod = mainClass.getMethod(MAIN_METHOD_NAME, new Class[]{String[].class});
            int modifiers = mainMethod.getModifiers();
            if (!Modifier.isPublic(modifiers) || !Modifier.isStatic(modifiers)) {
                throw new IllegalStateException(String.format(
                        "the main method of main class(%s) of App(%s) is NOT public static!",
                        mainClassName, appName));
            }

            Thread appMainThread = new AppThread(mainMethod, argArray, appName);
            appMainThread.setContextClassLoader(appClassLoader);
            appMainThread.start();
        } catch (Exception e) {
            throw new IllegalStateException(String.format("Fail to load app %s, cause: %s", appName, e.getMessage()), e);
        }
    }
}
