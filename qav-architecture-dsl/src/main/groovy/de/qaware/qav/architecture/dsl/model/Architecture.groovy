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

    /**
     * Return the name of the parent component, or null if not included or no parent.
     * Convenience method for {@link #getParentComponent(java.lang.String)}<tt>?.name</tt>
     *
     * @param name name of the class or component; may not be null
     * @return the name of the parent component, or null if not included or no parent
     */
    String getParentComponentName(String name) {
        checkNotNull(name)
        Component parentComponent = getParentComponent(name)

        return parentComponent?.name
    }

    /**
     * Return the parent component of the given class or component.
     * <tt>null</tt> if it is not included, or does not have a parent component.
     *
     * The "parent component" is the component <i>which contains</i> the given class or component.
     *
     * <ul>
     * <li>For classes, it's the component which contains it, as defined by {@link Component#isApi(java.lang.String)}
     * or {@link Component#isImpl(java.lang.String)}.</li>
     *
     * <li>For components, it's the parent component of the given component.</li>
     *
     * <li>If the given name is the name of the Architecture, i.e. the root node name, then the result is <tt>null</tt>.</li>
     *
     * <li>If the given name is excluded (as defined by {@link #includes} and {@link #excludes}, see {@link #isIncluded(java.lang.String)}
     * then the result is <tt>null</tt>.</li>
     *
     * <li>If the given name can't be found, neither in the component names, nor in the <tt>isApi</tt> or <tt>isImpl</tt>
     * definitions, the result is <tt>null</tt>.</li>
     * </ul>
     *
     * @param name name name of the class or component; may not be null
     * @return the parent component, or null if not included or no parent
     */
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
