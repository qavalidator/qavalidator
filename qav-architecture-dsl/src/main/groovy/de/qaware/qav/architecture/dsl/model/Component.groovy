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
    Map<String, ClassSet> uses = [:]
    String pathSeparator = null

    @Override
    String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("name", name)
                .append("parent.name", parent?.name)
                .append("children", children)
                .append("api", api)
                .append("impl", impl)
                .append("uses", uses)
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
        for (Map.Entry<String, ClassSet> entry : api.entrySet()) {
            if (entry.value.matches(name)) {
                return entry.key
            }
        }
        return null
    }

    boolean isApi(String name) {
        return (api.values().any {it.matches(name)})
    }

    boolean isImpl(String name) {
        return (impl.values().any {it.matches(name)})
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
     * returns a list of all component names that this {@link Component} is allowed to use.
     *
     * @return the list of component names
     */
    List<String> allUsesComponents() {
        List<String> result = []
        uses.values()*.getPatterns().each {
            result.addAll it
        }
        result
    }
}
