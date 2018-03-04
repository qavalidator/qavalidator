package de.qaware.qav.graph.filter;

import de.qaware.qav.graph.api.AbstractGraphElement;

import java.util.HashMap;
import java.util.Map;

/**
 * Filter on the basis of {@link AbstractGraphElement} properties.
 * <p>
 * This is an IN-filter, i.e. only if ALL properties in this filter are present and equal in the given node, the node is
 * accepted. If NOT ALL properties in this filter are present and equal in the given node, the node will not be
 * accepted.
 *
 * @param <T> the graph element type
 * @author QAware GmbH
 */
@SuppressWarnings("squid:S1694")
// warns that there is no abstract method, i.e. no abstract behaviour that is encapsulated here. However, this class
// contains the commonalities of filters working on AbstractGraphElement (i.e. Nodes and Dependencies) and is therefore fine.
public abstract class AbstractPropertyInFilter<T extends AbstractGraphElement> {

    private final Map<String, Object> properties = new HashMap<>();

    /**
     * Default constructor.
     */
    protected AbstractPropertyInFilter() {
    }

    /**
     * Value constructor. See {@link #addFilter(String, Object)}
     *
     * @param property property name
     * @param object   value
     */
    protected AbstractPropertyInFilter(String property, Object object) {
        addFilter(property, object);
    }

    /**
     * Add a property with required value to the filter
     *
     * @param property property name
     * @param object   value
     * @return <code>this</code>
     */
    public final AbstractPropertyInFilter<T> addFilter(String property, Object object) {
        properties.put(property, object);
        return this;
    }

    /**
     * decides whether the element will be accepted.
     *
     * @param graphElement the element to decide upon
     * @return <tt>true</tt> if the element will be in, <tt>false</tt> if it is out
     */
    public boolean isAccepted(T graphElement) {
        return properties.entrySet().stream()
                .allMatch(entry -> {
                    Object property = graphElement.getProperty(entry.getKey());
                    return property != null && property.equals(entry.getValue());
                });
    }
}
