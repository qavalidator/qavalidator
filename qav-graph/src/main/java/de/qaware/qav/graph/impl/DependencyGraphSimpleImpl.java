package de.qaware.qav.graph.impl;

import de.qaware.qav.graph.api.Dependency;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.EdgeFilter;
import de.qaware.qav.graph.api.Node;
import de.qaware.qav.graph.api.NodeFilter;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.graph.AbstractGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.MaskSubgraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation for {@link DependencyGraph} based on the JGraphT library.
 *
 * @author QAware GmbH
 */
@Slf4j
public class DependencyGraphSimpleImpl implements DependencyGraph {

    /**
     * Lookup table from name to node.
     */
    private final Map<String, Node> nodeMap = new HashMap<>();

    private AbstractGraph<Node, Dependency> graph = new DefaultDirectedGraph<>(Dependency.class);

    /**
     * The underlying graph. This is needed to add nodes or edges to filtered graphs. If this is the "original" graph,
     * then <tt>baseGraph</tt> points to <tt>this</tt>.
     */
    private final DependencyGraph baseGraph;

    /**
     * Constructor.
     */
    @SuppressWarnings("squid:S3366")
    // Sonar warns that "this" may be used by another thread before the object is completely created. In this setting
    // here, there won't be a problem.
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

    /**
     * Add a new dependency to the graph.
     * <p>
     * If there already is a dependency between these two nodes, the {@link DependencyType} is "upgraded" in case the
     * new dependency has a higher value (see {@link DependencyType#ordinal()}.
     *
     * @param from source
     * @param to   target
     * @param type type of the dependency
     * @return the new {@link Dependency}
     */
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

    /**
     * Filters the given collection to only those of the given {@link DependencyType}.
     */
    private Set<Dependency> filterDependencies(Set<Dependency> dependencies, DependencyType dependencyType) {
        return dependencies.stream()
                .filter(dep -> dep.getDependencyType() == dependencyType)
                .collect(Collectors.toSet());
    }

    @Override
    public DependencyGraph filter(final NodeFilter filter) {
        DependencyGraphSimpleImpl clone = new DependencyGraphSimpleImpl(this.baseGraph);
        clone.setGraph(
                new MaskSubgraph<>(graph,
                        node -> !filter.isAccepted(node),
                        edge -> false));

        return clone;
    }

    @Override
    public DependencyGraph filter(final EdgeFilter filter) {
        DependencyGraphSimpleImpl clone = new DependencyGraphSimpleImpl(this.baseGraph);
        clone.setGraph(
                new MaskSubgraph<>(graph,
                        node -> false,
                        dependency -> !filter.isAccepted(dependency)));

        return clone;
    }

    /**
     * Set a new JGraphT graph.
     * <p>
     * Put all nodes in {@link #nodeMap} so that the methods {@link #getNode(String)}, {@link #hasNode(String)} etc.
     * know about them.
     *
     * @param graph the {@link AbstractGraph}
     */
    protected void setGraph(AbstractGraph<Node, Dependency> graph) {
        this.graph = graph;

        for (Node node : graph.vertexSet()) {
            nodeMap.put(node.getName(), node);
        }
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
     * Only for those who know what they are doing. Handy to apply algorithms from the JGraphT library.
     *
     * @return the underlying JGraphT graph.
     */
    public AbstractGraph<Node, Dependency> getGraph() {
        return graph;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() +
                "[Nodes:" +
                graph.vertexSet().size() +
                "; Edges:" +
                graph.edgeSet().size() +
                "]";
    }
}
