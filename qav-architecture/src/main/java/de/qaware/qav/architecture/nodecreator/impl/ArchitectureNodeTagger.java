package de.qaware.qav.architecture.nodecreator.impl;

import de.qaware.qav.architecture.dsl.model.Architecture;
import de.qaware.qav.architecture.dsl.model.Component;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.Node;

/**
 * Write a tag on all nodes in the given graph and on all parent architecture nodes in that architecture view.
 * Will only tag those architecture nodes which are parents (grand-parents etc) of nodes in the given graph.
 * <p>
 * This is useful to tag a filtered graph and its architecture hierarchy.
 *
 * @author QAware GmbH
 */
public final class ArchitectureNodeTagger {

    /**
     * util class, no instances.
     */
    private ArchitectureNodeTagger() {
    }

    /**
     * Write a tag on all nodes in the given graph and on all architecture nodes in that architecture view.
     *
     * @param graph        the graph; often a filtered {@link DependencyGraph}
     * @param architecture the {@link Architecture}
     * @param tag          the tag to write to each node
     */
    public static void tagArchitectureNodes(DependencyGraph graph, Architecture architecture, String tag) {
        graph.getAllNodes()
                .forEach(node -> tagNode(graph.getBaseGraph(), node, architecture, tag));
    }

    /**
     * Tags the node and all its parents.
     * Goes up the hierarchy until it finds a parent component node which is already tagged.
     */
    private static void tagNode(DependencyGraph graph, Node node, Architecture architecture, String tag) {
        node.setProperty(tag, true);
        Component parentComponent = architecture.getParentComponent(node.getName());
        if (parentComponent != null) {
            Node parentNode = graph.getNode(parentComponent.getName());
            // only recurse if the parent has not yet been visited:
            if (parentNode != null && !parentNode.hasProperty(tag)) {
                tagNode(graph, parentNode, architecture, tag);
            }
        }
    }
}
