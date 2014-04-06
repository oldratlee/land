package com.oldratlee.land.matcher;

/**
 * ant-style pattern matcher.
 * <p/>
 * Pattern can be:
 * <p/>
 * <ol>
 * <li>a exactly full qualified class name. eg: {@code com.foo1.p0.ClassA}</li>
 * <li>a full qualified package name, end with one asterisk. eg: {@code com.foo1.p2.*}</li>
 * <li>a package and its sub packages, end with two asterisk. eg: {@code com.foo1.p3.**}</li>
 * </ol>
 *
 * @author ding.lid
 */
public class DefaultLandMatcher implements Matcher {
    @Override
    public boolean match(String className, String pattern) {
        return matchExactlyFullQualifiedClassName(className, pattern) ||
                matchFullQualifiedPackageNameAndItsSubPackage(className, pattern) ||
                matchFullQualifiedPackageName(className, pattern);
    }

    private boolean matchExactlyFullQualifiedClassName(String className, String pattern) {
        return className.equals(pattern);
    }

    private boolean matchFullQualifiedPackageNameAndItsSubPackage(String className, String pattern) {
        return pattern.endsWith(".**") &&
                className.startsWith(pattern.substring(0, pattern.length() - 2));
    }

    private boolean matchFullQualifiedPackageName(String className, String pattern) {
        return pattern.endsWith(".*") &&
                className.startsWith(pattern.substring(0, pattern.length() - 1)) &&
                !className.substring(pattern.length() - 1).contains(".");
    }

    @Override
    public boolean validate(String pattern) {
        // TODO add implementation
        return true;
    }
}
