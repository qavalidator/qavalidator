package de.qaware.qav.architecture.dsl.model

import org.apache.commons.lang3.builder.ToStringBuilder
import org.apache.commons.lang3.builder.ToStringStyle

/**
 * This class represents a Component in an architecture view.
 *
 * Structure: It has a name, a parent and 0..n children.
 *
 * Content: It represents a number of classes, identified by a list of regexp patterns.
 *
 * @author QAware GmbH
 */
class Component {

    String name
    Component parent
    List<Component> children = []
    Map<String, ClassSet> api = [:]
    Map<String, ClassSet> impl = [:]
    Map<String, ClassSet> usesAPI = [:]
    Map<String, ClassSet> usesImpl = [:]
    String pathSeparator = null

    @Override
    String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("name", name)
                .append("parent.name", parent?.name)
                .append("children", children)
                .append("api", api)
                .append("impl", impl)
                .append("usesAPI", usesAPI)
                .append("usesImpl", usesImpl)
                .append("pathSeparator", pathSeparator)
                .toString()
    }

    /**
     * return the name of the API the given class name belongs to, or null if none matches.
     *
     * @param name the class name
     * @return the name of the API the given class name belongs to, or null if none matches.
     */
    String getApiName(String name) {
        return findMatch(api, name)
    }

    /**
     * return the name of the Impl the given class name belongs to, or null if none matches.
     *
     * @param name the class name
     * @return the name of the Impl the given class name belongs to, or null if none matches.
     */
    String getImplName(String name) {
        return findMatch(impl, name)
    }

    boolean isApi(String name) {
        return getApiName(name) != null
    }

    boolean isImpl(String name) {
        return getImplName(name) != null
    }

    private String findMatch(Map<String, ClassSet> baseMap, String name) {
        for (Map.Entry<String, ClassSet> entry : baseMap.entrySet()) {
            if (entry.value.matches(name)) {
                return entry.key
            }
        }
        return null
    }

    /**
     * Get the pathSeparator for the ClassSet patterns, if defined.
     * If not, returns the pathSeparator defined for the parent class. If no pathSeparator is defined, returns null.
     *
     * @return the pathSeparator, or null.
     */
    String getPathSeparator() {
        if (pathSeparator) {
            return pathSeparator
        } else if (parent) {
            return parent.pathSeparator
        } else {
            return null
        }
    }

    /**
     * returns a list of all API names that this {@link Component} is allowed to use.
     *
     * @return the list of API names
     */
    List<String> allUsesAPIs() {
        List<String> result = []
        usesAPI.values()*.getPatterns().each {
            result.addAll it
        }
        result
    }

    /**
     * returns a list of all Impl names that this {@link Component} is allowed to use.
     *
     * @return the list of implementation names
     */
    List<String> allUsesImpl() {
        List<String> result = []
        usesImpl.values()*.getPatterns().each {
            result.addAll it
        }
        result
    }
}
