package de.qaware.qav.graph.filter;

import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.Node;
import de.qaware.qav.graph.api.NodeFilter;

/**
 * Accepts nodes which are accepted by the given filter or which have outgoing dependency to other nodes which are
 * accepted by the given filter.
 *
 * @author QAware GmbH
 */
public class NodeHasDependencyToFilter implements NodeFilter {

    private final DependencyGraph dependencyGraph;
    private final NodeFilter baseFilter;

    /**
     * Constructor.
     *
     * @param dependencyGraph the DependencyGraph to work on; may be a filtered graph
     * @param baseFilter      the filter which accepts nodes on outgoing edges
     */
    public NodeHasDependencyToFilter(DependencyGraph dependencyGraph, NodeFilter baseFilter) {
        this.dependencyGraph = dependencyGraph;
        this.baseFilter = baseFilter;
    }

    @Override
    public boolean isAccepted(Node node) {
        return baseFilter.isAccepted(node)
                || dependencyGraph.getOutgoingEdges(node).stream().anyMatch(dep -> baseFilter.isAccepted(dep.getTarget()));

    }
}
