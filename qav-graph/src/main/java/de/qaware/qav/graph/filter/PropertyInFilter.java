package de.qaware.qav.graph.filter;

import com.google.common.collect.Maps;
import de.qaware.qav.graph.api.AbstractGraphElement;

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
public abstract class PropertyInFilter<T extends AbstractGraphElement> {

    private final Map<String, Object> properties = Maps.newHashMap();

    /**
     * Default constructor.
     */
    protected PropertyInFilter() {
    }

    /**
     * Value constructor. See {@link #addFilter(String, Object)}
     *
     * @param property property name
     * @param object   value
     */
    protected PropertyInFilter(String property, Object object) {
        addFilter(property, object);
    }

    /**
     * Add a property with required value to the filter
     *
     * @param property property name
     * @param object   value
     * @return <code>this</code>
     */
    public final PropertyInFilter<T> addFilter(String property, Object object) {
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
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            Object property = graphElement.getProperty(entry.getKey());
            if (property == null || !entry.getValue().equals(property)) {
                return false;
            }
        }

        return true;
    }
}
