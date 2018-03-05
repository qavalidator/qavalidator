package de.qaware.qav.server.mapper;

import com.google.common.collect.Maps;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.Node;
import de.qaware.qav.server.model.DependencyDTO;
import de.qaware.qav.server.model.NodeDTO;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Mapper from {@link Node} to {@link NodeDTO}.
 * <p>
 * Maps the properties, the hierarchy (parents and children), and the dependencies (incoming and outgoing). Sorts all
 * entries alphabetically, for nice display at the UI.
 *
 * @author QAware GmbH
 */
public final class NodeMapper {

    /**
     * util class, no instances
     */
    private NodeMapper() {
    }

    /**
     * Map a {@link Node} to {@link NodeDTO}.
     *
     * @param node            the {@link Node}
     * @param dependencyGraph the {@link DependencyGraph}, required to find incoming and outgoing edges
     * @return the {@link NodeDTO}
     */
    public static NodeDTO toDTO(Node node, DependencyGraph dependencyGraph) {
        checkNotNull(node, "node");
        checkNotNull(dependencyGraph, "dependencyGraph");

        NodeDTO result = new NodeDTO();

        mapProperties(node, result);
        mapHierarchy(node, dependencyGraph, result);
        mapDependencies(result, node, dependencyGraph);

        return result;
    }

    private static void mapProperties(Node node, NodeDTO result) {
        result.setName(node.getName());
        result.setProperties(Maps.newTreeMap());
        result.getProperties().putAll(node.getProperties());
        result.getProperties().remove("name");
    }

    private static void mapHierarchy(Node node, DependencyGraph dependencyGraph, NodeDTO result) {
        List<DependencyDTO> parentDeps = dependencyGraph.getIncomingEdges(node, DependencyType.CONTAINS)
                .stream().map(DependencyMapper::toDTO)
                .sorted(Comparator.comparing(DependencyDTO::getSourceName))
                .collect(Collectors.toList());
        result.setParents(parentDeps);

        List<DependencyDTO> containedDeps = dependencyGraph.getOutgoingEdges(node, DependencyType.CONTAINS)
                .stream().map(DependencyMapper::toDTO)
                .sorted(Comparator.comparing(DependencyDTO::getTargetName))
                .collect(Collectors.toList());
        result.setContainedDeps(containedDeps);
    }

    private static void mapDependencies(NodeDTO result, Node node, DependencyGraph dependencyGraph) {
        List<DependencyDTO> incomingDeps = dependencyGraph.getIncomingEdges(node).stream()
                .filter(dependency -> dependency.getDependencyType() != DependencyType.CONTAINS)
                .map(DependencyMapper::toDTO)
                .sorted(Comparator.comparing(DependencyDTO::getSourceName))
                .collect(Collectors.toList());
        result.setIncomingDeps(incomingDeps);

        List<DependencyDTO> outgoingDeps = dependencyGraph.getOutgoingEdges(node).stream()
                .filter(dependency -> dependency.getDependencyType() != DependencyType.CONTAINS)
                .map(DependencyMapper::toDTO)
                .sorted(Comparator.comparing(DependencyDTO::getTargetName))
                .collect(Collectors.toList());
        result.setOutgoingDeps(outgoingDeps);
    }
}
