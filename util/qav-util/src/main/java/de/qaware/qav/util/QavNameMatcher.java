package de.qaware.qav.util;

import org.springframework.util.AntPathMatcher;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Pattern matching for fully qualified class names.
 *
 * @author QAware GmbH
 */
public class QavNameMatcher {

    private AntPathMatcher antPathMatcher;
    private final String pathSeparator;

    /**
     * Constructor. Use the default {@link #pathSeparator} with "."
     */
    public QavNameMatcher() {
        this.pathSeparator = ".";
    }

    /**
     * Constructor.
     *
     * @param pathSeparator the path separator
     */
    public QavNameMatcher(String pathSeparator) {
        this.pathSeparator = pathSeparator;
    }

    /**
     * returns <tt>true</tt> if the name matches the pattern, and false if it does not.
     * <p>
     * The pattern matching is Ant-style, with one difference: a ".*" at the end will be handled as ".**".
     *
     * @param pattern the pattern, Ant-style
     * @param name    the name to check
     * @return <tt>true</tt> if it matches, <tt>false</tt> if it does not.
     */
    public boolean matches(String pattern, String name) {
        checkNotNull(pattern, "pattern");
        checkNotNull(name, "name");

        if (pattern.equals("*") || pattern.endsWith(pathSeparator + "*")) {
            pattern += "*";
        }

        return getAntPathMatcher().match(pattern, name);
    }

    private AntPathMatcher getAntPathMatcher() {
        if (antPathMatcher == null) {
            antPathMatcher = new AntPathMatcher(pathSeparator);
        }
        return antPathMatcher;
    }
}
