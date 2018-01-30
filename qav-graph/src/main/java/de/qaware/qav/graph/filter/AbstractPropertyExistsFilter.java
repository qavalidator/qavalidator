package de.qaware.qav.graph.filter;

import com.google.common.collect.Sets;
import de.qaware.qav.graph.api.AbstractGraphElement;

import java.util.Set;

/**
 * Filter on the basis of {@link AbstractGraphElement} properties.
 * <p>
 * This is an IN-filter, i.e. only if ALL properties in this filter are present in the given node, the node is
 * accepted. If NOT ALL properties in this filter are present in the given node, the node will not be accepted.
 *
 * @param <T> the graph element type
 * @author QAware GmbH
 */
public abstract class AbstractPropertyExistsFilter<T extends AbstractGraphElement> {

    private final Set<String> propertyNames = Sets.newHashSet();

    /**
     * Default constructor.
     */
    public AbstractPropertyExistsFilter() {
    }

    /**
     * Value constructor. See {@link #addFilter(String)}
     *
     * @param property the property name to check for
     */
    public AbstractPropertyExistsFilter(String property) {
        addFilter(property);
    }

    /**
     * Add a property name to the filter
     *
     * @param property property name
     * @return <code>this</code>
     */
    public final AbstractPropertyExistsFilter addFilter(String property) {
        propertyNames.add(property);
        return this;
    }

    /**
     * decides whether the element will be accepted.
     *
     * @param graphElement the element to decide upon
     * @return <tt>true</tt> if the element will be in, <tt>false</tt> if it is out
     */
    public boolean isAccepted(T graphElement) {
        return propertyNames.stream().allMatch(key -> graphElement.getProperties().containsKey(key));
    }
}
