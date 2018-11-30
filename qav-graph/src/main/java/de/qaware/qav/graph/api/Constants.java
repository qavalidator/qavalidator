package de.qaware.qav.graph.api;

/**
 * This class contains constants to be used all over in the QAvalidator code.
 *
 * @author QAware GmbH
 */
public final class Constants {

    /**
     * property name for line numbers
     */
    public static final String LINE_NO = "lineNo";

    /**
     * property name for the type
     */
    public static final String TYPE = "type";

    /**
     * property value for {@link #TYPE} "class"
     */
    public static final String TYPE_CLASS = "class";

    /**
     * property value for {@link #TYPE} "method"
     */
    public static final String TYPE_METHOD = "method";

    /**
     * property name for annotations
     */
    public static final String ANNOTATION = "annotation";

    /**
     * property name for architecture
     */
    public static final String ARCHITECTURE = "architecture";

    /**
     * property name for "usesAPI" relation
     */
    public static final String USES_API = "uses-api";

    /**
     * property name for "usesImpl" relation
     */
    public static final String USES_IMPL = "uses-impl";

    /**
     * property name for used rule relation
     */
    public static final String USED_RULES = "used-rules";

    /**
     * property name for scope
     */
    public static final String SCOPE = "scope";

    /**
     * property name for a list of all architectures in which this class is unmapped
     */
    public static final String UNMAPPED = "architecture-unmapped";

    /**
     * suffix to build a property name for each architecture in which this class is unmapped:
     * It's <tt>architecture.getName() + UNMAPPED_SUFFIX</tt>
     */
    public static final String UNMAPPED_SUFFIX = "-unmapped";

    /**
     * suffix to build a property name for each architecture in which this class is ignored:
     * It's <tt>architecture.getName() + IGNORED_SUFFIX</tt>
     */
    public static final String IGNORED_SUFFIX = "-ignored";

    /**
     * suffix to build a property name for each architecture which tells the name of the parent component this class or
     * component belongs to:
     * <p>
     * It's <tt>architecture.getName() + PARENT_SUFFIX</tt>
     */
    public static final String PARENT_SUFFIX = "-parent";

    /**
     * suffix to build a property name for each architecture which tells the name of the API in the parent component
     * this class or component belongs to:
     * <p>
     * It's <tt>architecture.getName() + PARENT_API_SUFFIX</tt>
     */
    public static final String PARENT_API_SUFFIX = "-api";

    /**
     * suffix to build a property name for each architecture which tells the name of the Impl in the parent component
     * this class or component belongs to:
     * <p>
     * It's <tt>architecture.getName() + PARENT_IMPL_SUFFIX</tt>
     */
    public static final String PARENT_IMPL_SUFFIX = "-impl";

    /**
     * attribute name for the name of used target APIs.
     */
    public static final String TARGET_API = "uses-target-API";

    /**
     * attribute name for the name of used target Impls.
     */
    public static final String TARGET_IMPL = "uses-target-Impl";

    /**
     * attribute name for number number of base relations.
     */
    public static final String BASE_REL_COUNT = "baseRelCount";

    /**
     * attribute name for number number of different base relation targets.
     */
    public static final String BASE_REL_COUNT_TARGETS = "baseRelCountTargets";

    /**
     * attribute name for number number of different base relation sources.
     */
    public static final String BASE_REL_COUNT_SOURCES = "baseRelCountSources";

    /**
     * Util class, no instances.
     */
    private Constants() {
    }
}
