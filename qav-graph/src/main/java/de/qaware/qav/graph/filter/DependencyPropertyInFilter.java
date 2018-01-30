package de.qaware.qav.graph.filter;

import de.qaware.qav.graph.api.Dependency;
import de.qaware.qav.graph.api.EdgeFilter;

/**
 * Filter which only accepts edges which have the given properties and the exact values.
 *
 * @author QAware GmbH
 */
public class DependencyPropertyInFilter extends AbstractPropertyInFilter<Dependency> implements EdgeFilter {

    /**
     * Constructor.
     */
    public DependencyPropertyInFilter() {
        super();
    }

    /**
     * Value constructor. See {@link #addFilter(String, Object)}
     *
     * @param property the property name
     * @param object   the value to check for
     */
    public DependencyPropertyInFilter(String property, Object object) {
        super(property, object);
    }
}
