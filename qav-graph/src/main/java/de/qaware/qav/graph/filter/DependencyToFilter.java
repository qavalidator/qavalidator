package de.qaware.qav.graph.filter;

import de.qaware.qav.graph.api.Dependency;
import de.qaware.qav.graph.api.EdgeFilter;
import de.qaware.qav.graph.api.NodeFilter;

/**
 * Accepts edges which have target nodes which are accepted by the given filter.
 *
 * @author QAware GmbH
 */
public class DependencyToFilter implements EdgeFilter {

    private final NodeFilter baseFilter;

    /**
     * Constructor.
     *
     * @param baseFilter the filter which accepts target nodes
     */
    public DependencyToFilter(NodeFilter baseFilter) {
        this.baseFilter = baseFilter;
    }

    @Override
    public boolean isAccepted(Dependency edge) {
        return baseFilter.isAccepted(edge.getTarget());
    }

}
