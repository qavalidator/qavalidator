package de.qaware.qav.graph.filter;

import de.qaware.qav.graph.api.Node;
import de.qaware.qav.graph.api.NodeFilter;

/**
 * Filter which inverts the result of the given filter.
 *
 * @author QAware GmbH
 */
public class NotFilter implements NodeFilter {

    private final NodeFilter baseFilter;

    /**
     * Constructor.
     *
     * @param baseFilter the filter to invert
     */
    public NotFilter(NodeFilter baseFilter) {
        this.baseFilter = baseFilter;
    }

    @Override
    public boolean isAccepted(Node node) {
        return !baseFilter.isAccepted(node);
    }
}
