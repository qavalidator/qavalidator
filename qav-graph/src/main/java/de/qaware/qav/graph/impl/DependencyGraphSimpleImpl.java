package de.qaware.qav.graph.impl;

import com.google.common.collect.Maps;
import de.qaware.qav.graph.api.Dependency;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.EdgeFilter;
import de.qaware.qav.graph.api.Node;
import de.qaware.qav.graph.api.NodeFilter;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DirectedMaskSubgraph;
import org.jgrapht.graph.MaskFunctor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation for {@link DependencyGraph} based on the JGraphT library.
 *
 * @author QAware GmbH
 */
public class DependencyGraphSimpleImpl implements DependencyGraph {

    private static final Logger LOGGER = LoggerFactory.getLogger(DependencyGraphSimpleImpl.class);

    private final Map<String, Node> nodeMap = Maps.newHashMap();

    private DirectedGraph<Node, Dependency> graph = new DefaultDirectedGraph<>(Dependency.class);

    private final DependencyGraph baseGraph;

    /**
     * Constructor.
     */
    public DependencyGraphSimpleImpl() {
        baseGraph = this;
    }

    /**
     * Constructor.
     *
     * @param baseGraph the underlying graph, if this is a filtered graph.
     */
    public DependencyGraphSimpleImpl(DependencyGraph baseGraph) {
        this.baseGraph = baseGraph;
    }

    @Override
    public DependencyGraph getBaseGraph() {
        return baseGraph;
    }

    @Override
    public Node getOrCreateNodeByName(String name) {
        Node node = nodeMap.get(name);
        if (node == null) {
            node = new Node(name);
            addNode(node);
        }
        return node;
    }

    @Override
    public Node getNode(String name) {
        return nodeMap.get(name);
    }

    private void addNode(Node node) {
        nodeMap.put(node.getName(), node);
        graph.addVertex(node);
    }

    @Override
    public boolean hasNode(String name) {
        return nodeMap.containsKey(name);
    }

    @Override
    public Dependency addDependency(Node from, Node to, DependencyType type) {
        Dependency result;

        Dependency edge = graph.getEdge(from, to);
        if (edge != null) {
            if (edge.getDependencyType().ordinal() < type.ordinal()) {
                LOGGER.debug("Upgrading dependency {} to [{}]", edge, type);
                edge.setDependencyType(type);
            } else {
                LOGGER.debug("Reusing dependency {}", edge);
            }
            result = edge;
        } else {
            result = new Dependency(from, to, type);
            graph.addEdge(from, to, result);
        }

        return result;
    }

    @Override
    public Dependency getEdge(Node from, Node to) {
        return graph.getEdge(from, to);
    }

    @Override
    public Set<Dependency> getOutgoingEdges(Node node) {
        return graph.outgoingEdgesOf(node);
    }

    @Override
    public Set<Dependency> getOutgoingEdges(Node node, DependencyType dependencyType) {
        return filterDependencies(getOutgoingEdges(node), dependencyType);
    }

    @Override
    public Set<Dependency> getIncomingEdges(Node node) {
        return graph.incomingEdgesOf(node);
    }

    @Override
    public Set<Dependency> getIncomingEdges(Node node, DependencyType dependencyType) {
        return filterDependencies(getIncomingEdges(node), dependencyType);
    }

    protected void setGraph(DirectedGraph<Node, Dependency> graph) {
        this.graph = graph;

        for (Node node : graph.vertexSet()) {
            nodeMap.put(node.getName(), node);
        }
    }

    @Override
    public DependencyGraph filter(final NodeFilter filter) {
        DependencyGraphSimpleImpl clone = new DependencyGraphSimpleImpl(this.baseGraph);
        clone.setGraph(new DirectedMaskSubgraph<>(graph, new MaskFunctor<Node, Dependency>() {
            @Override
            public boolean isEdgeMasked(Dependency edge) {
                return !filter.isAccepted(edge.getSource()) || !filter.isAccepted(edge.getTarget());
            }

            @Override
            public boolean isVertexMasked(Node vertex) {
                return !filter.isAccepted(vertex);
            }
        }));

        return clone;
    }

    @Override
    public DependencyGraph filter(final EdgeFilter filter) {
        DependencyGraphSimpleImpl clone = new DependencyGraphSimpleImpl(this.baseGraph);
        clone.setGraph(new DirectedMaskSubgraph<>(graph, new MaskFunctor<Node, Dependency>() {
            @Override
            public boolean isEdgeMasked(Dependency edge) {
                return !filter.isAccepted(edge);
            }

            @Override
            public boolean isVertexMasked(Node vertex) {
                return false;
            }
        }));

        return clone;
    }

    @Override
    public Collection<Node> getAllNodes() {
        return new ArrayList<>(graph.vertexSet());
    }

    @Override
    public Collection<Dependency> getAllEdges() {
        return new ArrayList<>(graph.edgeSet());
    }

    /**
     * filters the given collection.
     */
    private Set<Dependency> filterDependencies(Set<Dependency> dependencies, DependencyType dependencyType) {
        return dependencies
                .stream()
                .filter(dep -> dep.getDependencyType().equals(dependencyType))
                .collect(Collectors.toSet());
    }

    /**
     * Only for those who know what they are doing. Handy to apply algorithms.
     *
     * @return the underlying JGraphT graph.
     */
    public DirectedGraph<Node, Dependency> getGraph() {
        return graph;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(this.getClass().getSimpleName())
                .append("[Nodes:")
                .append(graph.vertexSet().size())
                .append("; Edges:")
                .append(graph.edgeSet().size())
                .append("]")
                .toString();
    }
}
