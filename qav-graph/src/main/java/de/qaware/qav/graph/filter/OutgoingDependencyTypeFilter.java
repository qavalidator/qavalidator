package de.qaware.qav.graph.filter;

import de.qaware.qav.graph.api.Dependency;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.Node;
import de.qaware.qav.graph.api.NodeFilter;

import java.util.Set;

/**
 * Accepts nodes which have an outgoing edge of the given type.
 *
 * @author QAware GmbH
 */
public class OutgoingDependencyTypeFilter implements NodeFilter {

    private final DependencyGraph dependencyGraph;
    private final DependencyType dependencyType;

    /**
     * Constructor.
     *
     * @param dependencyGraph the {@link DependencyGraph}
     * @param dependencyType  the {@link DependencyType} to be included
     */
    public OutgoingDependencyTypeFilter(DependencyGraph dependencyGraph, DependencyType dependencyType) {
        this.dependencyGraph = dependencyGraph;
        this.dependencyType = dependencyType;
    }

    @Override
    public boolean isAccepted(Node node) {
        Set<Dependency> outgoingEdges = dependencyGraph.getOutgoingEdges(node);
        for (Dependency dep : outgoingEdges) {
            if (dep.getDependencyType().equals(dependencyType)) {
                return true;
            }
        }

        return false;
    }
}
