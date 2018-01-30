package de.qaware.qav.graph.filter;

import de.qaware.qav.graph.api.Dependency;
import de.qaware.qav.graph.api.EdgeFilter;

/**
 * Filter which only accepts edges which have the given properties set.
 *
 * @author QAware GmbH
 */
public class DependencyPropertyExistsFilter extends AbstractPropertyExistsFilter<Dependency> implements EdgeFilter {

    /**
     * Constructor.
     */
    public DependencyPropertyExistsFilter() {
        super();
    }

    /**
     * Value constructor. See {@link #addFilter(String)}
     *
     * @param property the property to check for
     */
    public DependencyPropertyExistsFilter(String property) {
        super(property);
    }
}
