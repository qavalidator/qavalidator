package de.qaware.qav.architecture.nodecreator;

import de.qaware.qav.architecture.dsl.model.Architecture;
import de.qaware.qav.architecture.dsl.model.Component;
import de.qaware.qav.architecture.tagger.ComponentNameTagger;
import de.qaware.qav.graph.api.Constants;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.Node;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Creates architecture nodes, i.e. nodes which represent architecture components.
 * They are hierarchic and build a tree, based on {@link de.qaware.qav.graph.api.Dependency} relations of type
 * {@link DependencyType#CONTAINS}.
 *
 * @author QAware GmbH
 */
public final class ArchitectureNodeCreator {

    /**
     * util class, no instances.
     */
    private ArchitectureNodeCreator() {
    }

    /**
     * Triggers the node creation.
     *
     * @param dependencyGraph the source graph; typically a filtered graph of the full graph
     * @param architecture    the architecture to use
     * @return the list of all unmapped classes. Will not be null. If empty, all classes could be mapped.
     */
    public static List<String> createAllArchitectureNodes(DependencyGraph dependencyGraph, Architecture architecture) {
        checkNotNull(dependencyGraph, "dependencyGraph");
        checkNotNull(architecture, "architecture");
        DependencyGraph baseGraph = dependencyGraph.getBaseGraph();

        // tag all nodes in the given graph (which may be a filtered graph, i.e. a subset of the full graph) with the
        // component name, and find unmapped classes:
        List<String> unmappedClasses = ComponentNameTagger.tagComponentNames(dependencyGraph, architecture);

        createArchitectureNodes(baseGraph, architecture, architecture.getName());
        dependencyGraph.getAllNodes().forEach(node -> mapClassToComponents(baseGraph, node, architecture.getName()));

        return unmappedClasses;
    }

    /**
     * Create a node for the given component, and recursively for all its children.
     * Connect the component node and its children with a {@link DependencyType#CONTAINS} relation.
     */
    private static Node createArchitectureNodes(DependencyGraph baseGraph, Component component, String architectureName) {
        Node node = baseGraph.getOrCreateNodeByName(component.getName());
        node.setProperty(Constants.TYPE, "architecture");
        node.setProperty(Constants.ARCHITECTURE, architectureName);
        node.setProperty(architectureName, true);
        node.setProperty(Constants.USES_API, component.allUsesAPIs());
        node.setProperty(Constants.USES_IMPL, component.allUsesImpl());

        for (Component child : component.getChildren()) {
            Node childNode = createArchitectureNodes(baseGraph, child, architectureName);
            baseGraph.addDependency(node, childNode, DependencyType.CONTAINS);
        }

        return node;
    }

    /**
     * Create the {@link DependencyType#CONTAINS} relation between the architecture component and the leaf.
     */
    private static void mapClassToComponents(DependencyGraph baseGraph, Node node, String architectureName) {
        String componentName = (String) node.getProperty(architectureName + Constants.PARENT_SUFFIX);
        if (componentName != null) {
            Node componentNode = baseGraph.getNode(componentName);
            baseGraph.addDependency(componentNode, node, DependencyType.CONTAINS);
        }
    }
}
