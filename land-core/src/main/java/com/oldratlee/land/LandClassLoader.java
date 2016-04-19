package com.oldratlee.land;

import com.oldratlee.land.matcher.DefaultLandMatcher;
import com.oldratlee.land.matcher.LandMatcher;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 * Land ClassLoader.
 *
 * @author ding.lid
 */
public class LandClassLoader extends URLClassLoader {
    public static final String CLASS = "class ";
    private final Map<DelegateType, List<String>> delegateConfig;
    private final LandMatcher matcher;

    public LandClassLoader(URL[] urls, Map<DelegateType, List<String>> delegateConfig) {
        this(urls, delegateConfig, null, null);
    }

    public LandClassLoader(URL[] urls, Map<DelegateType, List<String>> delegateConfig, ClassLoader parent) {
        this(urls, delegateConfig, null, parent);
    }

    public LandClassLoader(URL[] urls, Map<DelegateType, List<String>> delegateConfig, LandMatcher matcher) {
        this(urls, delegateConfig, matcher, null);
    }

    /**
     * Delegate config format, key is {@link DelegateType}, value is string list, item can be below case:
     * <p/>
     * <ol>
     * <li>specify one class, content is a full qualified class name. example: {@code "com.foo.package1.Class1"}, {@code "com.foo.package1.Class2$InnerClass2"}</li>
     * <li>specify a package, content is a full qualified package name end by a dot. example: {@code "com.foo.package2.*"}</li>
     * <li>specify a package and its sub packages, content is a full qualified package name end by two dot. example: {@code "com.foo.package3.**"}</li>
     * </ol>
     * <p/>
     * Delegate config example:
     * <pre><code>{
     *     NONE : [com.foo1.p0.ClassA, com.foo1.p1.ClassB, com.foo1.p2.*, com.foo1.p3.**],
     *     CHILD_ONLY : [com.foo2.p0.*Model, com.foo2.p1.*Impl, com.foo2.p2.*Spi*]
     * }</code></pre>
     *
     * @param urls           class path
     * @param delegateConfig delegate config.
     * @param matcher        class name matcher
     * @param parent         parent classloader
     */
    public LandClassLoader(URL[] urls, Map<DelegateType, List<String>> delegateConfig, LandMatcher matcher, ClassLoader parent) {
        super(urls, parent == null ? getSystemClassLoader() : parent);

        if (delegateConfig == null) {
            delegateConfig = new EnumMap<>(DelegateType.class);
        }
        this.matcher = matcher == null ? new DefaultLandMatcher() : matcher;
        for (Map.Entry<DelegateType, List<String>> typeEntry : delegateConfig.entrySet()) {
            for (String pattern : typeEntry.getValue()) {
                this.matcher.validate(pattern);
            }
        }
        this.delegateConfig = delegateConfig;
    }

    static {
        ClassLoader.registerAsParallelCapable();
    }

    @Override
    protected Class<?> loadClass(String className, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(className)) {
            Class c = loadClass0(className);
            if (resolve) {
                resolveClass(c);
            }
            return c;
        }
    }

    private Class loadClass0(String className) throws ClassNotFoundException {
        // 0. check if the class has already been loaded
        Class c = findLoadedClass(className);
        if (c != null) {
            return c;
        }

        // 1. check if the class is in system class loader
        try {
            return getSystemClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            // ClassNotFoundException thrown if class not found
        }

        // 2. get class according to delegate config
        DelegateType delegateType = findDelegateTypeInDelegateConfigs(className, delegateConfig, matcher);
        ClassLoader parent = getParent();
        switch (delegateType) {
            case NONE:
                throw new ClassNotFoundException(CLASS + className + "(NONE) is forbidden by land config!");
            case PARENT_ONLY:
                return parentOnly(className, parent);
            case CHILD_ONLY:
                // TODO improve exception message
                return findClass(className);
            case PARENT_CHILD:
                return parentChild(className, parent);
            case CHILD_PARENT:
                return childParent(className, parent);
            default:
                throw new IllegalStateException("Unsupported delegate config " + delegateType);
        }
    }

    private Class parentOnly(String className, ClassLoader parent) throws ClassNotFoundException {
        if (parent == getSystemClassLoader()) {
            throw new ClassNotFoundException(CLASS + className + "(PARENT_ONLY) not found in parent class loader");
        } else {
            // TODO improve exception message
            return parent.loadClass(className);
        }
    }

    private Class parentChild(String className, ClassLoader parent) throws ClassNotFoundException {
        // parent does not load this class before
        if (parent != getSystemClassLoader()) {
            try {
                return parent.loadClass(className);
            } catch (ClassNotFoundException e) {
                // ClassNotFoundException thrown if class not found
            }
        }
        return findClass(className);
    }

    private Class childParent(String className, ClassLoader parent) throws ClassNotFoundException {
        try {
            return findClass(className);
        } catch (ClassNotFoundException e) {
            // ClassNotFoundException thrown if class not found
        }
        if (parent != getSystemClassLoader()) {
            try {
                return parent.loadClass(className);
            } catch (ClassNotFoundException e) {
                // ClassNotFoundException thrown if class not found
            }
        }
        throw new ClassNotFoundException(CLASS + className + "(CHILD_PARENT) not found in parent class loader");
    }

    static DelegateType findDelegateTypeInDelegateConfigs(String className, Map<DelegateType, List<String>> delegateConfig, LandMatcher matcher) {
        for (Map.Entry<DelegateType, List<String>> entry : delegateConfig.entrySet()) {
            if (matchPatterns(className, entry.getValue(), matcher)) {
                return entry.getKey();
            }
        }
        return DelegateType.PARENT_CHILD;
    }

    static boolean matchPatterns(String className, List<String> patterns, LandMatcher matcher) {
        for (String pattern : patterns) {
            if (matcher.match(className, pattern)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return LandClassLoader.class.getSimpleName() + "(urls: " + Arrays.toString(getURLs()) + ")";
    }
}
