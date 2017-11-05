package de.qaware.qav.graph.filter;

import de.qaware.qav.graph.api.Node;
import de.qaware.qav.graph.api.NodeFilter;

/**
 * Filter which only accepts nodes which have the given properties set.
 *
 * @author QAware GmbH
 */
public class NodePropertyExistsFilter extends PropertyExistsFilter<Node> implements NodeFilter {

    /**
     * Constructor.
     */
    public NodePropertyExistsFilter() {
    }

    /**
     * Value constructor. See {@link #addFilter(String)}
     *
     * @param property the property name to check for
     */
    public NodePropertyExistsFilter(String property) {
        super(property);
    }
}
