package de.qaware.qav.server.mapper;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.Node;
import de.qaware.qav.server.model.DependencyDTO;
import de.qaware.qav.server.model.NodeDTO;

import java.util.ArrayList;
import java.util.Collections;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Mapper from {@link Node} to {@link NodeDTO}.
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
        ArrayList<DependencyDTO> parentDeps = Lists.newArrayList();
        dependencyGraph.getIncomingEdges(node, DependencyType.CONTAINS)
                .forEach(dependency -> parentDeps.add(DependencyMapper.toDTO(dependency)));
        Collections.sort(parentDeps, (o1, o2) -> o1.getSourceName().compareTo(o2.getSourceName()));
        result.setParents(parentDeps);

        ArrayList<DependencyDTO> containedDeps = Lists.newArrayList();
        dependencyGraph.getOutgoingEdges(node, DependencyType.CONTAINS)
                .forEach(dependency -> containedDeps.add(DependencyMapper.toDTO(dependency)));
        Collections.sort(containedDeps, (o1, o2) -> o1.getTargetName().compareTo(o2.getTargetName()));
        result.setContainedDeps(containedDeps);
    }

    private static void mapDependencies(NodeDTO result, Node node, DependencyGraph dependencyGraph) {
        ArrayList<DependencyDTO> incomingDeps = Lists.newArrayList();
        dependencyGraph.getIncomingEdges(node).stream()
                .filter(dependency -> dependency.getDependencyType() != DependencyType.CONTAINS)
                .forEach(dependency -> incomingDeps.add(DependencyMapper.toDTO(dependency)));
        Collections.sort(incomingDeps, (o1, o2) -> o1.getSourceName().compareTo(o2.getSourceName()));
        result.setIncomingDeps(incomingDeps);

        ArrayList<DependencyDTO> outgoingDeps = Lists.newArrayList();
        dependencyGraph.getOutgoingEdges(node).stream()
                .filter(dependency -> dependency.getDependencyType() != DependencyType.CONTAINS)
                .forEach(dependency -> outgoingDeps.add(DependencyMapper.toDTO(dependency)));
        Collections.sort(outgoingDeps, (o1, o2) -> o1.getTargetName().compareTo(o2.getTargetName()));
        result.setOutgoingDeps(outgoingDeps);
    }
}
