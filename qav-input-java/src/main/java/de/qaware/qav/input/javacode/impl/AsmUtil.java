package de.qaware.qav.input.javacode.impl;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.objectweb.asm.Type;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Utility methods to deal with ASM Strings.
 *
 * @author QAware GmbH
 */
public final class AsmUtil {

    private static final List<Pair<Pattern, String>> REPLACEMENTS = Lists.newArrayList();

    static {
        addReplacement("^L", "");
        addReplacement("\\[L", "");
        addReplacement("^[\\[]*", "");
        addReplacement(";$", "");
        addReplacement("\\[\\]", "");
        addReplacement("/", ".");
    }

    /**
     * util class is not meant to be instantiated.
     */
    private AsmUtil() {
    }

    private static void addReplacement(String pattern, String rep) {
        REPLACEMENTS.add(new ImmutablePair<>(Pattern.compile(pattern), rep));
    }

    /**
     * Returns the type names of the parameters.
     *
     * @param desc                 the ASM description
     * @param collapseInnerClasses Flag if inner classes are to be collapsed
     * @return the type names
     */
    public static List<String> getParameterTypeNames(String desc, boolean collapseInnerClasses) {
        return Arrays.stream(Type.getArgumentTypes(desc))
                .map(t -> toClassName(t.getClassName(), collapseInnerClasses))
                .collect(Collectors.toList());
    }

    /**
     * Returns cleaned name of class or outer class, if inner classes should be collapsed.
     *
     * @param className            Name of the class
     * @param collapseInnerClasses Flag if inner classes are to be collapsed
     * @return The cleaned name of the class
     */
    public static String toClassName(String className, boolean collapseInnerClasses) {
        if (className == null) {
            return null;
        }
        if (collapseInnerClasses && className.indexOf('$') >= 0) {
            return toClassName(className.substring(0, className.indexOf('$')));
        }
        return toClassName(className);
    }

    /**
     * Returns cleaned class name.
     *
     * @param className The name of the class
     * @return The cleaned name of the class
     */
    private static String toClassName(String className) {
        if (className == null) {
            return null;
        }

        String s = className;
        for (Pair<Pattern, String> pair : REPLACEMENTS) {
            s = pair.getLeft().matcher(s).replaceAll(pair.getRight());
        }

        return s;
    }

}
