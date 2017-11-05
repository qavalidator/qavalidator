package de.qaware.qav.architecture.dsl.impl

import de.qaware.qav.architecture.dsl.model.Architecture
import de.qaware.qav.architecture.dsl.model.ClassSet
import groovy.util.logging.Slf4j

/**
 * An Architecture definition, created from the QAvalidator Architecture DSL.
 *
 * See {@link ComponentSpec}.
 *
 * An Architecture is a Component, but it can only appear as root, while components may be nested.
 *
 * @author QAware GmbH
 */
@Slf4j
class ArchitectureSpec extends ComponentSpec {

    ArchitectureSpec(Architecture architecture) {
        super(architecture)
    }

    private Architecture getArchitecture() {
        return component as Architecture
    }

    /*
     * keyword "includes"
     */

    void includes(String... patterns) {
        log.debug("component includes: ${patterns}")
        ClassSet set = getClassSet(getArchitecture().includes, name)
        set.addPatterns(patterns)
    }

    void includes(Map<String, String[]> values) {
        log.debug("includes definition: ${values}")
        values.each {includesName, patterns ->
            ClassSet set = getClassSet(getArchitecture().includes, includesName)
            set.addPatterns(patterns)
        }
    }

    /*
     * keyword "excludes"
     */

    void excludes(String... patterns) {
        log.debug("component excludes: ${patterns}")
        ClassSet set = getClassSet(getArchitecture().excludes, name)
        set.addPatterns(patterns)
    }

    void excludes(Map<String, String[]> values) {
        log.debug("excludes definition: ${values}")
        values.each {excludesName, patterns ->
            ClassSet set = getClassSet(getArchitecture().excludes, excludesName)
            set.addPatterns(patterns)
        }
    }

    /**
     * keyword "ignore"
     *
     * @param patterns the patterns to exclude
     * @deprecated use <tt>excludes</tt> instead.
     */
    void ignore(String... patterns) {
        log.debug("component ignore: ${patterns}")
        // increase to WARN as soon as deprecation roadmap is fixed.
        log.debug("The 'ignore' keyword will be deprecated in one of the next releases. Use 'excludes' instead.")
        excludes(patterns)
    }
}
