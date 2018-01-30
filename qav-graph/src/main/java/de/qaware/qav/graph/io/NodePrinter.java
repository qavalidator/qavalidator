package de.qaware.qav.graph.io;

import de.qaware.qav.graph.api.Dependency;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.Node;
import org.apache.commons.io.IOUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Prints nodes.
 *
 * @author QAware GmbH
 */
public class NodePrinter {

    private final DependencyGraph dependencyGraph;
    private final File outputFile;

    private PrintWriter out;

    /**
     * Constructor.
     *
     * @param dependencyGraph the {@link DependencyGraph} with all the nodes to print out.
     *                        Uses the {@link DependencyGraph#getBaseGraph()} to print the edges to/from other nodes
     *                        outside the filtered scope of this graph.
     * @param filename        the file name to write to
     */
    public NodePrinter(DependencyGraph dependencyGraph, String filename) {
        this.dependencyGraph = dependencyGraph;
        this.outputFile = new File(filename);
    }

    /**
     * Prints all nodes in the graph: Their name, their properties, and their incoming and outgoing edges.
     * Uses the {@link DependencyGraph#getBaseGraph()} to print the edges to/from other nodes outside the
     * filtered scope of this graph.
     */
    public void printNodes() {
        FileWriter fw;
        try {
            fw = new FileWriter(this.outputFile, false);
        } catch (IOException e) {
            throw new IllegalStateException("Error writing to file " + this.outputFile.getAbsolutePath(), e);
        }

        BufferedWriter bw = new BufferedWriter(fw);
        this.out = new PrintWriter(bw);

        this.out.print("");
        dependencyGraph.getAllNodes().forEach(this::printNode);

        IOUtils.closeQuietly(out);
        IOUtils.closeQuietly(bw);
        IOUtils.closeQuietly(fw);
    }

    /**
     * Prints the given node: Its name, its properties, and its incoming and outgoing edges.
     *
     * @param node the node.
     */
    private void printNode(Node node) {
        if (node == null) {
            return;
        }
        out.format("name: %s%n", node.getName());

        Map<String, Object> properties = node.getProperties();
        if (properties.size() > 1) { // one property is always there: the name
            out.format("    Node Properties:%n");
            properties.forEach((key, value) -> {
                        if (!"name".equals(key)) {
                            out.format("        %s: %s%n", key, value);
                        }
                    }
            );
        }

        List<Dependency> outEdges = new ArrayList<>(dependencyGraph.getBaseGraph().getOutgoingEdges(node));
        if (!outEdges.isEmpty()) {
            out.format("    OUTGOING -->%n");
            outEdges.sort(Comparator.comparing(o -> o.getTarget().getName()));
            outEdges.forEach(dep -> out.format("        %s[%s]%n",dep.getTarget().getName(), dep.getDependencyType().name()));
        }

        List<Dependency> inEdges = new ArrayList<>(dependencyGraph.getBaseGraph().getIncomingEdges(node));
        if (!inEdges.isEmpty()) {
            out.format("    INCOMING <--%n");
            inEdges.sort(Comparator.comparing(o -> o.getSource().getName()));
            inEdges.forEach(dep -> out.format("        %s[%s]%n", dep.getSource().getName(), dep.getDependencyType().name()));
        }
    }
}
