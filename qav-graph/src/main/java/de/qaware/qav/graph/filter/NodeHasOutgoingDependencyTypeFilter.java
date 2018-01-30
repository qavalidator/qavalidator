package de.qaware.qav.graph.filter;

import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.Node;
import de.qaware.qav.graph.api.NodeFilter;

/**
 * Accepts nodes which have an outgoing edge of the given type.
 *
 * @author QAware GmbH
 */
public class NodeHasOutgoingDependencyTypeFilter implements NodeFilter {

    private final DependencyGraph dependencyGraph;
    private final DependencyType dependencyType;

    /**
     * Constructor.
     *
     * @param dependencyGraph the {@link DependencyGraph}
     * @param dependencyType  the {@link DependencyType} to be included
     */
    public NodeHasOutgoingDependencyTypeFilter(DependencyGraph dependencyGraph, DependencyType dependencyType) {
        this.dependencyGraph = dependencyGraph;
        this.dependencyType = dependencyType;
    }

    @Override
    public boolean isAccepted(Node node) {
        return dependencyGraph.getOutgoingEdges(node).stream().anyMatch(dep -> dep.getDependencyType().equals(dependencyType));
    }
}
