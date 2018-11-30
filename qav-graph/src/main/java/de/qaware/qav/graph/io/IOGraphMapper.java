package de.qaware.qav.graph.io;

import de.qaware.qav.graph.api.Dependency;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.Node;
import de.qaware.qav.graph.factory.DependencyGraphFactory;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Maps between a {@link DependencyGraph} and an {@link IOGraph}.
 *
 * @author QAware GmbH
 */
public class IOGraphMapper {

    /**
     * Maps a {@link DependencyGraph} to a {@link IOGraph}.
     *
     * @param graph the {@link DependencyGraph}
     * @return the {@link IOGraph}
     */
    public IOGraph createIOGraph(DependencyGraph graph) {
        IOGraph result = new IOGraph();

        result.setNodes(createIOGraphNodes(graph.getAllNodes()));
        result.setEdges(createIOGraphEdges(graph.getAllEdges()));

        return result;
    }

    /**
     * Nodes are represented as a map of their properties.
     * <p>
     * Sort the nodes according to their names, so that the output file is the reproducibly the same for the same
     * graph.
     *
     * @param nodes {@link Node}s
     * @return the list of property maps
     */
    private List<Map<String, Object>> createIOGraphNodes(Collection<Node> nodes) {
        return nodes.stream()
                .map(this::mapNode)
                .sorted(Comparator.comparing(o -> ((String) o.get("name"))))
                .collect(Collectors.toList());
    }

    private Map<String, Object> mapNode(Node node) {
        TreeMap<String, Object> result = new TreeMap<>();
        node.getProperties().forEach((key, value) -> result.put(key, mapValue(value)));

        return result;
    }

    private Object mapValue(Object value) {
        if (value instanceof List) {
            return value;
        } else if (value instanceof Integer || value instanceof Long || value instanceof Boolean) {
            return value;
        } else {
            return value.toString();
        }
    }

    /**
     * Create the edges in the {@link IOGraph}. Sort them according to the names of the connected nodes, so that the
     * created output file is reproducibly the same for the same graph.
     *
     * @param edges the edges in the graph
     * @return the list of {@link IOEdge}s.
     */
    private List<IOEdge> createIOGraphEdges(Collection<Dependency> edges) {
        return edges.stream()
                .map(it -> this.mapEdge(it, true))
                .sorted(Comparator.comparing(o -> o.getFrom() + '#' + o.getTo()))
                .collect(Collectors.toList());
    }

    private IOEdge mapEdge(Dependency edge, boolean mapBaseDependencies) {
        IOEdge result = new IOEdge();

        result.setFrom(edge.getSource().getName());
        result.setTo(edge.getTarget().getName());
        result.setType(edge.getDependencyType().name());

        if (mapBaseDependencies) {
            TreeMap<String, Object> properties = new TreeMap<>();
            edge.getProperties().forEach((key, value) -> properties.put(key, mapValue(value)));
            result.setProps(properties);

            List<IOEdge> baseDependencies = edge.getBaseDependencies().stream()
                    .map(it -> mapEdge(it, false))
                    .sorted(Comparator.comparing(o -> o.getFrom() + '#' + o.getTo()))
                    .collect(Collectors.toList());

            result.setBaseDependencies(baseDependencies);
        }

        return result;
    }

    /**
     * Maps an {@link IOGraph} to a {@link DependencyGraph}.
     * <p>
     * First add the nodes, then the dependencies, before setting the baseDependencies relations.
     *
     * @param ioGraph the {@link IOGraph}
     * @return the {@link DependencyGraph}
     */

    public DependencyGraph createDependencyGraph(IOGraph ioGraph) {
        DependencyGraph dependencyGraph = DependencyGraphFactory.createGraph();

        addNodes(dependencyGraph, ioGraph);
        addDependencies(dependencyGraph, ioGraph);
        addBaseDependencies(dependencyGraph, ioGraph);

        return dependencyGraph;
    }

    private void addNodes(DependencyGraph dependencyGraph, IOGraph ioGraph) {
        ioGraph.getNodes().forEach(nodeMap -> {
            Node node = dependencyGraph.getOrCreateNodeByName((String) nodeMap.get("name"));

            nodeMap.forEach((key, value) -> {
                if (!"name".equals(key)) {
                    node.setProperty(key, value);
                }
            });
        });
    }

    private void addDependencies(DependencyGraph dependencyGraph, IOGraph ioGraph) {
        ioGraph.getEdges().forEach(ioEdge -> {
            Node fromNode = dependencyGraph.getNode(ioEdge.getFrom());
            Node toNode = dependencyGraph.getNode(ioEdge.getTo());
            DependencyType type = DependencyType.valueOf(ioEdge.getType());
            Dependency dep = dependencyGraph.addDependency(fromNode, toNode, type);

            if (ioEdge.getProps() != null) {
                ioEdge.getProps().forEach(dep::setProperty);
            }
        });
    }

    private void addBaseDependencies(DependencyGraph dependencyGraph, IOGraph ioGraph) {
        ioGraph.getEdges().forEach(ioEdge -> {
            Node fromNode = dependencyGraph.getNode(ioEdge.getFrom());
            Node toNode = dependencyGraph.getNode(ioEdge.getTo());
            Dependency newDep = dependencyGraph.getEdge(fromNode, toNode);
            doAddBaseDependencies(dependencyGraph, newDep, ioEdge);
        });
    }

    private static void doAddBaseDependencies(DependencyGraph dependencyGraph, Dependency newDep, IOEdge ioEdge) {
        if (ioEdge != null && ioEdge.getBaseDependencies() != null) {
            ioEdge.getBaseDependencies().forEach(edge -> {
                Node fromNode = dependencyGraph.getNode(edge.getFrom());
                Node toNode = dependencyGraph.getNode(edge.getTo());

                Dependency baseDep = dependencyGraph.getEdge(fromNode, toNode);
                newDep.addBaseDependency(baseDep);

                doAddBaseDependencies(dependencyGraph, baseDep, edge);
            });
        }
    }
}
