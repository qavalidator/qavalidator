package de.qaware.qav.input.javacode;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.objectweb.asm.Type;

import java.util.List;
import java.util.regex.Pattern;

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
     * returns the type names of the parameters
     *
     * @param desc                 the Asm description
     * @param collapseInnerClasses Flag if inner classes are to be collapsed
     * @return the type names
     */
    public static List<String> getParameterTypeNames(String desc, boolean collapseInnerClasses) {
        List<String> paramTypes = Lists.newArrayList();
        Type[] argumentTypes = Type.getArgumentTypes(desc);
        for (Type t : argumentTypes) {
            paramTypes.add(toClassName(t.getClassName(), collapseInnerClasses));
        }
        return paramTypes;
    }

    /**
     * Returns cleaned name of class or outer class, if inner classes should be collapsed
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
     * Returns cleaned class name
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
