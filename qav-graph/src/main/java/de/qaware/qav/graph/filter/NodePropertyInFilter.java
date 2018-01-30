package de.qaware.qav.graph.filter;

import de.qaware.qav.graph.api.Node;
import de.qaware.qav.graph.api.NodeFilter;

/**
 * Filter which only accepts nodes which have the given properties and the exact values.
 *
 * @author QAware GmbH
 */
public class NodePropertyInFilter extends AbstractPropertyInFilter<Node> implements NodeFilter {

    /**
     * Constructor.
     */
    public NodePropertyInFilter() {
    }

    /**
     * Value constructor. See {@link #addFilter(String, Object)}
     *
     * @param property the property name
     * @param object   the value to check for
     */
    public NodePropertyInFilter(String property, Object object) {
        super(property, object);
    }
}
