package de.qaware.qav.graphdb.persistence;

import com.google.common.base.Strings;
import de.qaware.qav.graph.api.Dependency;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.Node;
import de.qaware.qav.graphdb.model.AbstractNode;
import de.qaware.qav.graphdb.model.ArchitectureNode;
import de.qaware.qav.graphdb.model.ClassNode;
import de.qaware.qav.graphdb.model.ReferencesRelation;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Maps {@link DependencyGraph} to a graph mapped into Neo4j.
 */
@Slf4j
public class GraphMapper {

    /** Key to the property map in DependencyGraph node properties. */
    public static final String ARCHITECTURE_KEY = "architecture";

    private final Map<String, AbstractNode> nodeMap = new HashMap<>();
    private final List<ReferencesRelation> referencesRelations = new ArrayList<>();

    /**
     * Map the given {@link DependencyGraph} to the nodes mapped into Neo4j. The nodes and the relations can be queried
     * via the getters.
     *
     * @param graph the {@link DependencyGraph}
     */
    public void toNeo4j(DependencyGraph graph) {
        graph.getAllNodes().stream()
                .map(this::createNode)
                .forEach(n -> nodeMap.put(n.getName(), n));

        graph.getAllEdges()
                .forEach(this::mapEdge);

        LOGGER.info("Mapped graph with {} nodes and {} edges", nodeMap.size(), referencesRelations.size());
    }

    /**
     * Returns the nodes.
     *
     * @return the nodes
     */
    public Collection<AbstractNode> getNodes() {
        return nodeMap.values();
    }

    /**
     * Returns the relations.
     *
     * @return the relations
     */
    public List<ReferencesRelation> getReferencesRelations() {
        return referencesRelations;
    }

    private AbstractNode createNode(Node n) {
        AbstractNode result;

        String architectureName = (String) n.getProperty(ARCHITECTURE_KEY);
        if (Objects.equals(n.getProperty("type"), ARCHITECTURE_KEY)) {
            result = new ArchitectureNode(n.getName());
            ((ArchitectureNode) result).setArchitectureName(architectureName);
        } else {
            result = new ClassNode(n.getName());
        }

        mapProperties(result.getProperties(), n.getProperties());
        // remove entries which are unnecessary
        result.getProperties().remove("name");
        result.getProperties().remove("type");

        result.getProperties().remove(ARCHITECTURE_KEY);

        if (!Strings.isNullOrEmpty(architectureName)) {
            result.getProperties().remove(architectureName);
        }
        return result;
    }

    private void mapEdge(Dependency dependency) {
        if (dependency.getDependencyType() == DependencyType.CONTAINS) {
            mapHierarchy(dependency);
        } else {
            mapReference(dependency);
        }
    }

    private void mapHierarchy(Dependency dependency) {
        ArchitectureNode from = (ArchitectureNode) nodeMap.get(dependency.getSource().getName());
        AbstractNode to = nodeMap.get(dependency.getTarget().getName());

        if (to instanceof ArchitectureNode) {
            ArchitectureNode child = (ArchitectureNode) to;
            from.getChildren().add(child);
            child.setParent(from);
        } else {
            ClassNode impl = (ClassNode) to;
            from.getImplementations().add(impl);
            impl.getImplementationFor().add(from);
        }

    }

    private void mapReference(Dependency dependency) {
        ReferencesRelation referencesRelation = new ReferencesRelation();
        referencesRelation.setReferenceType(dependency.getDependencyType().name());

        referencesRelation.setFrom(nodeMap.get(dependency.getSource().getName()));
        referencesRelation.setTo(nodeMap.get(dependency.getTarget().getName()));

        referencesRelation.setLineNo(dependency.getProperty("lineNo", new ArrayList<>()));
        mapProperties(referencesRelation.getProperties(), dependency.getProperties());
        referencesRelation.getProperties().remove("lineNo");

        referencesRelation.getFrom().getReferencesRelations().add(referencesRelation);

        referencesRelations.add(referencesRelation);
    }

    private void mapProperties(Map<String, Object> target, Map<String, Object> source) {
        source.forEach((k, v) -> target.put(k, mapObject(v)));
    }

    @SuppressWarnings("unchecked")
    private Object mapObject(Object o) {
        if (o instanceof String || o instanceof Long) {
            return o;
        } else if (o instanceof Integer) {
            return ((Integer) o).longValue(); // OGM can't handle Integer but Long.
        } else if (o instanceof List) {
            return mapPropertyList((List<Object>) o); // unchecked cast
        } else if (o instanceof Boolean) {
            return Boolean.toString((Boolean) o);
        } else {
            LOGGER.warn("What's here: value {} of type {}", o, o.getClass().getName());
            return o.toString();
        }
    }

    private List<Object> mapPropertyList(List<Object> list) {
        return list.stream()
                .map(this::mapObject) // recursive call
                .collect(Collectors.toList());
    }
}
