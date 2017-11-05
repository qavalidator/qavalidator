package de.qaware.qav.graph.api;

/**
 * Dependency types.
 *
 * They are ordered according to the "strength" or "tightness" of the relationship.
 * A higher {@link #ordinal} value indicates tighter coupling.
 *
 * @author QAware GmbH
 */
public enum DependencyType {

    // ===== Java Scope dependencies =====

    /**
     * a class references another class
     */
    REFERENCE,

    /**
     * access only Getters (judged on naming conventions)
     */
    READ_ONLY,

    /**
     * carries an annotation
     */
    ANNOTATED_BY,

    /**
     * received via <tt>@Autowired</tt> annotation
     */
    INJECTED,

    /**
     * access any kind of methods, i.e. potentially changes values in the other object
     */
    READ_WRITE,

    /**
     * calls a constructor
     */
    CREATE,

    /**
     * inherits from the other class
     */
    INHERIT,

    // ===== For maven dependencies =====

    /**
     * depends with scope "test"
     */
    TEST,

    /**
     * depends with scope "provided"
     */

    PROVIDED,

    /**
     * depends with scope "runtime"
     */
    RUNTIME,

    /**
     * depends with scope "compile"
     */
    COMPILE,

    // ===== generally used =====

    /**
     * For architecture node relationship
     */
    CONTAINS
}
