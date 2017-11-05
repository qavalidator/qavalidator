package de.qaware.qav.graph.api;

import java.util.Collection;
import java.util.Set;

/**
 * A Dependency Graph contains a node for each element and an edge for each dependency. Nodes are of type {@link Node},
 * edges of type {@link Dependency}.
 *
 * @author QAware GmbH
 */
public interface DependencyGraph {

    /**
     * Returns all nodes of the graph.
     *
     * @return all nodes of the graph.
     */
    Collection<Node> getAllNodes();

    /**
     * Returns all edges of the graph.
     *
     * @return all edges of the graph.
     */
    Collection<Dependency> getAllEdges();

    /**
     * Returns an existing or a new node with the given name.
     *
     * @param name name.
     *
     * @return the node: either it existed before, or it was created. Never <tt>null</tt>.
     */
    Node getOrCreateNodeByName(String name);

    /**
     * Returns the name with the given name, or <tt>null</tt> if it does not exist in the graph.
     *
     * @param name the name.
     *
     * @return the node or <tt>null</tt>.
     */
    Node getNode(String name);

    /**
     * Checks if the node with the given name exists.
     *
     * @param name the name.
     *
     * @return true if a node with this name exists, false if not.
     */
    boolean hasNode(String name);

    /**
     * Adds a dependency with the given type from the <tt>from</tt>-node to the <tt>to</tt>-node.
     *
     * @param from source
     * @param to   target
     * @param type type of the dependency
     *
     * @return the new dependency
     */
    Dependency addDependency(Node from, Node to, DependencyType type);

    /**
     * Get the edge between the given nodes.
     *
     * @param from source node
     * @param to   target node
     *
     * @return the edge between these nodes, or null if there is no edge.
     */
    Dependency getEdge(Node from, Node to);

    /**
     * Get all outgoing edges from the given source node.
     *
     * @param node the source node.
     *
     * @return all outgoing edges. May be empty, but not null.
     */
    Set<Dependency> getOutgoingEdges(Node node);

    /**
     * Get all outgoing edges from the given source node with the given DependencyType
     *
     * @param node           the source node.
     * @param dependencyType the DependencyType
     *
     * @return all outgoing edges with the given type. May be empty, but not null.
     */
    Set<Dependency> getOutgoingEdges(Node node, DependencyType dependencyType);

    /**
     * Get all incoming edges to the given source node.
     *
     * @param node the target node.
     *
     * @return all incoming edges. May be empty, but not null.
     */
    Set<Dependency> getIncomingEdges(Node node);

    /**
     * Get all incoming edges from the given source node with the given DependencyType
     *
     * @param node           the target node.
     * @param dependencyType the DependencyType
     *
     * @return all incoming edges with the given type. May be empty, but not null.
     */
    Set<Dependency> getIncomingEdges(Node node, DependencyType dependencyType);

    /**
     * Returns a {@link DependencyGraph} which only contains the nodes accepted by the given filter.
     *
     * @param filter node filter to apply.
     *
     * @return a graph which only contains the nodes accepted by the filter.
     */
    DependencyGraph filter(NodeFilter filter);

    /**
     * Returns a {@link DependencyGraph} which only contains the edges accepted by the given filter.
     *
     * @param filter edges filter to apply.
     *
     * @return a graph which only contains the edges accepted by the filter.
     */
    DependencyGraph filter(EdgeFilter filter);

    /**
     * Returns the underlying graph, which may be used for inserting new nodes or edges.
     *
     * @return the underlying graph, or <code>this</code> if it is modifyable.
     */
    DependencyGraph getBaseGraph();
}
