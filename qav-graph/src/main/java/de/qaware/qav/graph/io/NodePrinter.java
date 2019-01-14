package de.qaware.qav.graph.io;

import de.qaware.qav.graph.api.Dependency;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.Node;
import de.qaware.qav.util.FileSystemUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Prints nodes in plain text into a file.
 *
 * @author QAware GmbH
 */
public class NodePrinter {

    private final DependencyGraph dependencyGraph;
    private final String filename;

    /**
     * Constructor.
     *
     * @param dependencyGraph the {@link DependencyGraph} with all the nodes to print out. Uses the {@link
     *                        DependencyGraph#getBaseGraph()} to print the edges to/from other nodes outside the
     *                        filtered scope of this graph.
     * @param filename        the file name to write to
     */
    public NodePrinter(DependencyGraph dependencyGraph, String filename) {
        this.dependencyGraph = dependencyGraph;
        this.filename = filename;
    }

    /**
     * Prints all nodes in the graph: Their name, their properties, and their incoming and outgoing edges.
     * <p>
     * Uses the {@link DependencyGraph#getBaseGraph()} to print the edges to/from other nodes outside the filtered scope
     * of this graph.
     * <p>
     * Creates the file, if it didn't exist. Overwrites it if it did.
     */
    public void printNodes() {
        StringBuilder sb = new StringBuilder();
        dependencyGraph.getAllNodes().forEach(n -> printNode(n, sb));
        FileSystemUtil.writeStringToFile(sb.toString(), filename);
    }

    /**
     * Prints the given node: Its name, its properties, and its incoming and outgoing edges.
     * <p>
     * Omits empty blocks, i.e. only prints properties if there are any etc.
     *
     * @param node the node to print
     */
    private void printNode(Node node, StringBuilder sb) {
        sb.append(String.format("name: %s%n", node.getName()));

        Map<String, Object> properties = node.getProperties();
        if (properties.size() > 1) { // one property is always there: the name
            sb.append(String.format("    Node Properties:%n"));
            properties.entrySet().stream()
                    .filter(entry -> !"name".equals(entry.getKey()))
                    .forEach(entry -> sb.append(String.format("        %s: %s%n", entry.getKey(), entry.getValue())));
        }

        List<Dependency> outEdges = new ArrayList<>(dependencyGraph.getBaseGraph().getOutgoingEdges(node));
        if (!outEdges.isEmpty()) {
            sb.append(String.format("    OUTGOING -->%n"));
            outEdges.sort(Comparator.comparing(o -> o.getTarget().getName()));
            outEdges.forEach(dep -> sb.append(String.format("        %s[%s]%n", dep.getTarget().getName(), dep.getDependencyType().name())));
        }

        List<Dependency> inEdges = new ArrayList<>(dependencyGraph.getBaseGraph().getIncomingEdges(node));
        if (!inEdges.isEmpty()) {
            sb.append(String.format("    INCOMING <--%n"));
            inEdges.sort(Comparator.comparing(o -> o.getSource().getName()));
            inEdges.forEach(dep -> sb.append(String.format("        %s[%s]%n", dep.getSource().getName(), dep.getDependencyType().name())));
        }
    }
}
