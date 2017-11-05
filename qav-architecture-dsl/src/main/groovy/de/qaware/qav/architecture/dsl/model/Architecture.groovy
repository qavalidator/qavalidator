package de.qaware.qav.architecture.dsl.model

import com.google.common.collect.Maps
import org.apache.commons.lang3.builder.ToStringBuilder
import org.apache.commons.lang3.builder.ToStringStyle

import static com.google.common.base.Preconditions.checkNotNull

/**
 * Represents an Architecture View.
 *
 * An Architecture is a Component, but it can only appear as root, while components may be nested.
 * It has includes/excludes patterns which components do not have.
 *
 * @author QAware GmbH
 */
class Architecture extends Component {

    String prefix
    String reflexMLversion

    Map<String, ClassSet> includes = [:]
    Map<String, ClassSet> excludes = [:]

    List<Component> allComponents = []
    Map<String, Component> nameToComponent = [:]
    Map<String, Component> apiNameToComponent = [:]
    Map<String, Component> implNameToComponent = [:]

    /**
     * Cache for class names
     */
    private Map<String, Component> nameToParentComponentCache = Maps.newHashMap()

    @Override
    String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .appendSuper(Object.toString())
                .append("prefix", prefix)
                .append("reflexMLversion", reflexMLversion)
                .append("includes", includes)
                .append("excludes", excludes)
                .toString()
    }

    String getRootNodeName() {
        return name
    }

    String getParentComponentName(String name) {
        checkNotNull(name)
        Component parentComponent = getParentComponent(name)

        return parentComponent?.name
    }

    Component getParentComponent(String name) {
        checkNotNull(name)
        if (name == this.name) {
            return null
        }
        if (!isIncluded(name)) {
            return null
        }

        Component cachedComponent = nameToParentComponentCache[name]
        if (cachedComponent) {
            return cachedComponent
        }

        if (nameToComponent.containsKey(name)) {
            Component component = nameToComponent.get(name)
            nameToParentComponentCache[name] = component.parent
            return component.parent
        }

        for (Component component : allComponents) {
            if (component.isApi(name) || component.isImpl(name)) {
                nameToParentComponentCache[name] = component
                return component
            }
        }

        return null
    }

    /**
     * Decides whether a class name is "in" or not. A class name is "in" if it is included and not excluded:
     *
     * <ul>
     * <li>it is included if no <tt>includes</tt> pattern is given OR it is matched by at least one of the
     * <tt>includes</tt> patterns.</li>
     * <li>it is excluded if <tt>excludes</tt> patterns are given, AND a least one <tt>excludes</tt> pattern matches
     * the name.</li>
     * </ul>
     *
     * A name is also "in" if it is the name of a {@link Component} in this {@link Architecture}.
     *
     * @param name the class name to check
     * @return true if "in", false if not
     */
    boolean isIncluded(String name) {
        boolean included = (!includes) || includes.values().any {it.matches((name))}
        boolean excluded = excludes && excludes.values().any {it.matches((name))}

        return (included && !excluded) || nameToComponent.containsKey(name)
    }
}
