package de.qaware.qav.input.javacode.impl;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Filter out ignorable dependencies. We're not interested in dependencies to <tt>java.lang.*</tt>
 * and to primitive (or boxed) types.
 *
 * @author QAware GmbH
 */
public final class DependencyUtil {

    private static final Set<String> IGNORE_CLASSNAMES = Sets.newHashSet(
            "java.lang.Object",
            "B", "Z", "C", "D", "F", "I", "J", "S", "V",
            "byte", "boolean", "char", "double", "float", "int", "long", "short", "void");

    /**
     * Util class, no instances.
     */
    private DependencyUtil() {
    }

    /**
     * checks if the given class name may be ignored. We're not interested in dependencies to <tt>java.lang.*</tt>
     * and to primitive (or boxed) types.
     *
     * @param className the class name.
     * @return true if the class may be ignored.
     */
    @SuppressWarnings("squid:S4248") // wants to move the RegExes into constants; would help readability here.
    public static boolean isIgnorable(String className) {
        if (className == null) {
            return true;
        }

        String name = className.replaceAll("/", ".")
                .replaceAll("\\[\\]", "");
        return IGNORE_CLASSNAMES.contains(name) || name.startsWith("java.lang.");
    }
}
