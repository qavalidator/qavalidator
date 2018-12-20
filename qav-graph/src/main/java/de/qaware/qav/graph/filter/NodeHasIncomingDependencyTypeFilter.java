package de.qaware.qav.graph.filter;

import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.Node;
import de.qaware.qav.graph.api.NodeFilter;

/**
 * Accepts nodes which have an incoming edge of the given type.
 *
 * @author QAware GmbH
 */
public class NodeHasIncomingDependencyTypeFilter implements NodeFilter {

    private final DependencyGraph dependencyGraph;
    private final DependencyType dependencyType;

    /**
     * Value constructor. Accepts nodes which have an incoming edge of the given type.
     *
     * @param dependencyGraph the {@link DependencyGraph}
     * @param dependencyType  the {@link DependencyType}
     */
    public NodeHasIncomingDependencyTypeFilter(DependencyGraph dependencyGraph, DependencyType dependencyType) {
        this.dependencyGraph = dependencyGraph;
        this.dependencyType = dependencyType;
    }

    @Override
    public boolean isAccepted(Node node) {
        return dependencyGraph.getIncomingEdges(node).stream().anyMatch(dep -> dep.getDependencyType() == dependencyType);
    }
}
