package com.oldratlee.land;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Land ClassLoader.
 *
 * @author ding.lid
 */
public class LandClassLoader extends URLClassLoader {
    public static enum DelegateType {
        NONE,
        CHILD_ONLY,
        PARENT_ONLY,
        PARENT_CHILD,
        CHILD_PARENT
    }

    private final Map<DelegateType, List<String>> delegateConfig;

    public LandClassLoader(Map<DelegateType, List<String>> delegateConfig, URL[] urls) {
        super(urls);
        if (delegateConfig == null) {
            delegateConfig = new HashMap<>();
        }
        this.delegateConfig = delegateConfig;
    }

    /**
     * Delegate config format, key is {@link com.oldratlee.land.LandClassLoader.DelegateType}, value is string list, item can be below case:
     * <p/>
     * <ol>
     * <li>specify one class, content is a full qualified class name. example: {@code "com.foo.package1.Class1"}, {@code "com.foo.package1.Class2$InnerClass2"}</li>
     * <li>specify a package, content is a full qualified package name end by a dot. example: {@code "com.foo.package2."}</li>
     * <li>specify a package and its sub packages, content is a full qualified package name end by two dot. example: {@code "com.foo.package3.."}</li>
     * </ol>
     *
     * @param delegateConfig delegate config.
     * @param urls           class path
     * @param parent         parent classloader
     */
    public LandClassLoader(Map<DelegateType, List<String>> delegateConfig, URL[] urls, ClassLoader parent) {
        super(urls, parent);
        if (delegateConfig == null) {
            delegateConfig = new HashMap<>();
        }
        this.delegateConfig = delegateConfig;
    }

    static {
        ClassLoader.registerAsParallelCapable();
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            // First, check if the class has already been loaded
            Class c = findLoadedClass(name);
            if (c == null) {
                // Check System Loader first
                try {
                    c = getSystemClassLoader().loadClass(name);
                } catch (ClassNotFoundException e) {
                    // ClassNotFoundException thrown if class not found
                }

                if (c == null) {
                    DelegateType delegateType = findDelegateType(name, delegateConfig);
                    ClassLoader parent = getParent();
                    switch (delegateType) {
                        case NONE:
                            throw new ClassNotFoundException("class " + name + "(NONE) is forbidden by land config!");
                        case PARENT_ONLY:
                            if (parent == getSystemClassLoader()) {
                                throw new ClassNotFoundException("class " + name + "(PARENT_ONLY) not found in parent class loader");
                            } else {
                                // TODO improve exception message
                                c = parent.loadClass(name);
                            }
                            break;
                        case CHILD_ONLY:
                            // TODO improve exception message
                            c = findClass(name);
                            break;
                        case PARENT_CHILD:
                            // parent does not load this class before
                            if (parent != getSystemClassLoader()) {
                                try {
                                    c = parent.loadClass(name);
                                } catch (ClassNotFoundException e) {
                                    // ClassNotFoundException thrown if class not found
                                }
                            }
                            if (null == c) {
                                // TODO improve exception message
                                c = findClass(name);
                            }
                            break;
                        case CHILD_PARENT:
                            try {
                                c = findClass(name);
                            } catch (ClassNotFoundException e) {
                                // ClassNotFoundException thrown if class not found
                            }
                            if (c == null && parent != getSystemClassLoader()) {
                                try {
                                    c = parent.loadClass(name);
                                } catch (ClassNotFoundException e) {
                                    // ClassNotFoundException thrown if class not found
                                }
                            }
                            if (null == c) {
                                throw new ClassNotFoundException("class " + name + "(CHILD_PARENT) not found in parent class loader");
                            }
                            break;
                    }
                }
            }
            if (resolve) {
                resolveClass(c);
            }
            return c;
        }
    }

    static DelegateType findDelegateType(String name, Map<DelegateType, List<String>> delegateConfig) {
        for (Map.Entry<DelegateType, List<String>> entry : delegateConfig.entrySet()) {
            if (match(name, entry.getValue())) {
                return entry.getKey();
            }
        }
        return DelegateType.PARENT_CHILD;
    }

    static boolean match(String name, List<String> matches) {
        for (String m : matches) {
            if (match(name, m)) {
                return true;
            }
        }
        return false;
    }

    static boolean match(String name, String m) {
        // TODO check illegal matches!
        return name.equals(m)
                || m.endsWith("..") && name.startsWith(m.substring(0, m.length() - 1))
                || m.endsWith(".") && name.startsWith(m) && !name.substring(m.length() + 1).contains(".");
    }

    @Override
    public String toString() {
        return LandClassLoader.class.getSimpleName() + "(urls: " + Arrays.toString(getURLs()) + ")";
    }
}
