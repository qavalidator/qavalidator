package de.qaware.qav.architecture.dsl.model

import com.google.common.base.Strings
import de.qaware.qav.util.QavNameMatcher
import groovy.transform.ToString

/**
 *
 * @author QAware GmbH
 */
@ToString(includeNames=true, includePackage = false, includes = ['name', 'patterns'])
class ClassSet {

    private final String name
    private final List<String> patterns = new ArrayList<>()

    private QavNameMatcher nameMatcher
    String pathSeparator = "."

    /**
     * Constructor.
     *
     * @param name the name of this set
     */
    ClassSet(String name) {
        this.name = name
    }

    /**
     * Constructor.
     *
     * @param name the name of this set
     * @param patterns the list if include patterns. Optional.
     */
    ClassSet(String name, List<String> patterns) {
        this.name = name
        this.addPatterns(patterns)
    }

    /**
     * add the given patterns to this {@link ClassSet}. Both in the original form and in the compiled form.
     *
     * @param newPatterns the new patterns to add
     */
    final void addPatterns(String... newPatterns) {
        for (String newPattern : newPatterns) {
            addPattern(newPattern)
        }
    }

    /**
     * add the given patterns to this {@link ClassSet}. Both in the original form and in the compiled form.
     *
     * @param newPatterns the new patterns to add
     */
    final void addPatterns(List<String> newPatterns) {
        newPatterns.each {addPattern(it)}
    }

    private void addPattern(String pattern) {
        this.patterns.add(pattern)
    }

    String getName() {
        return name
    }

    List<String> getPatterns() {
        return patterns
    }

    /**
     * Checks if the given class name matches any of the include patterns, using {@link QavNameMatcher}.
     *
     * @param className the class name to check
     * @return true if it matches any of the include patterns
     */
    boolean matches(String className) {
        if (Strings.isNullOrEmpty(className)) {
            return false
        }
        for (String p : patterns) {
            if (getNameMatcher().matches(p, className)) {
                return true
            }
        }
        return false
    }

    private QavNameMatcher getNameMatcher() {
        if (!nameMatcher) {
            nameMatcher = new QavNameMatcher(pathSeparator)
        }
        return nameMatcher
    }

    boolean equals(o) {
        if (o == null) {
            return false
        }
        if (this.is(o)) {
            return true
        }
        if (getClass() != o.class) {
            return false
        }

        ClassSet classSet = (ClassSet) o

        if (name != classSet.name
                || patterns != classSet.patterns) {
            return false
        }

        return true
    }

    int hashCode() {
        int result
        result = (name != null ? name.hashCode() : 0)
        result = 31 * result + (patterns != null ? patterns.hashCode() : 0)
        return result
    }
}
