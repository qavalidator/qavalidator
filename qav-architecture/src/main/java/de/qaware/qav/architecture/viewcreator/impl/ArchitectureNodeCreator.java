package de.qaware.qav.architecture.viewcreator.impl;

import de.qaware.qav.architecture.dsl.model.Architecture;
import de.qaware.qav.architecture.dsl.model.Component;
import de.qaware.qav.graph.api.Constants;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.Node;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Creates architecture nodes, i.e. nodes which represent architecture components. They are hierarchic and build a tree,
 * based on {@link de.qaware.qav.graph.api.Dependency} relations of type {@link DependencyType#CONTAINS}.
 *
 * @author QAware GmbH
 */
public final class ArchitectureNodeCreator {

    private final DependencyGraph dependencyGraph;
    private final Architecture architecture;
    private final DependencyGraph baseGraph;
    private final String architectureName;

    /**
     * Constructor.
     *
     * @param dependencyGraph the source graph; typically a filtered graph of the full graph
     * @param architecture    the architecture to use
     */
    public ArchitectureNodeCreator(DependencyGraph dependencyGraph, Architecture architecture) {
        this.dependencyGraph = checkNotNull(dependencyGraph, "dependencyGraph");
        this.architecture = checkNotNull(architecture, "architecture");

        this.baseGraph = dependencyGraph.getBaseGraph();
        this.architectureName = architecture.getName();
    }

    /**
     * Traverses the {@link Architecture} tree and creates a node for each component.
     *
     * @return the list of all unmapped classes. Will not be null. If empty, all classes could be mapped.
     */
    public List<String> createAllArchitectureNodes() {
        // tag all nodes in the given graph (which may be a filtered graph, i.e. a subset of the full graph) with the
        // component name, and find unmapped classes:
        List<String> unmappedClasses = ComponentNameTagger.tagComponentNames(dependencyGraph, architecture);

        createArchitectureNodes(architecture);
        dependencyGraph.getAllNodes().forEach(this::mapClassToComponents);

        return unmappedClasses;
    }

    /**
     * Create a node for the given component, and recursively for all its children. Connect the component node and its
     * children with a {@link DependencyType#CONTAINS} relation.
     */
    private Node createArchitectureNodes(Component component) {
        Node node = baseGraph.getOrCreateNodeByName(component.getName());
        setNodeProperties(node, component);

        for (Component child : component.getChildren()) {
            Node childNode = createArchitectureNodes(child);
            baseGraph.addDependency(node, childNode, DependencyType.CONTAINS);
        }

        return node;
    }

    /**
     * Set the properties on the node.
     *
     * @param node      the node
     * @param component the component it represents
     */
    private void setNodeProperties(Node node, Component component) {
        node.setProperty(Constants.TYPE, "architecture");
        node.setProperty(Constants.ARCHITECTURE, architectureName);
        node.setProperty(architectureName, true);
        node.setProperty(Constants.USES_API, component.allUsesAPIs());
        node.setProperty(Constants.USES_IMPL, component.allUsesImpl());
    }

    /**
     * Create the {@link DependencyType#CONTAINS} relation between the architecture component and the leaf.
     */
    private void mapClassToComponents(Node node) {
        String componentName = (String) node.getProperty(architectureName + Constants.PARENT_SUFFIX);
        if (componentName != null) {
            Node componentNode = baseGraph.getNode(componentName);
            baseGraph.addDependency(componentNode, node, DependencyType.CONTAINS);
        }
    }
}
