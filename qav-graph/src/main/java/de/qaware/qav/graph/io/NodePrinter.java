package de.qaware.qav.graph.io;

import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;
import de.qaware.qav.graph.api.Dependency;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.Node;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
    private final File outputFile;

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
        this.outputFile = new File(filename);
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
        try {
            Files.asCharSink(this.outputFile, StandardCharsets.UTF_8).write("");
            dependencyGraph.getAllNodes().forEach(this::printNode);
        } catch (IOException e) {
            throw new IllegalStateException("Error writing to file " + this.outputFile.getAbsolutePath(), e);
        }
    }

    /**
     * Prints the given node: Its name, its properties, and its incoming and outgoing edges.
     * <p>
     * Omits empty blocks, i.e. only prints properties if there are any etc.
     *
     * @param node the node to print
     */
    private void printNode(Node node) {
        if (node == null) {
            return;
        }
        append(String.format("name: %s%n", node.getName()));

        Map<String, Object> properties = node.getProperties();
        if (properties.size() > 1) { // one property is always there: the name
            append(String.format("    Node Properties:%n"));
            properties.forEach((key, value) -> {
                        if (!"name".equals(key)) {
                            append(String.format("        %s: %s%n", key, value));
                        }
                    }
            );
        }

        List<Dependency> outEdges = new ArrayList<>(dependencyGraph.getBaseGraph().getOutgoingEdges(node));
        if (!outEdges.isEmpty()) {
            append(String.format("    OUTGOING -->%n"));
            outEdges.sort(Comparator.comparing(o -> o.getTarget().getName()));
            outEdges.forEach(dep -> append(String.format("        %s[%s]%n", dep.getTarget().getName(), dep.getDependencyType().name())));
        }

        List<Dependency> inEdges = new ArrayList<>(dependencyGraph.getBaseGraph().getIncomingEdges(node));
        if (!inEdges.isEmpty()) {
            append(String.format("    INCOMING <--%n"));
            inEdges.sort(Comparator.comparing(o -> o.getSource().getName()));
            inEdges.forEach(dep -> append(String.format("        %s[%s]%n", dep.getSource().getName(), dep.getDependencyType().name())));
        }
    }

    /**
     * Appends a string to the output file {@link #outputFile}.
     *
     * @param s the string
     */
    private void append(String s) {
        try {
            Files.asCharSink(this.outputFile, StandardCharsets.UTF_8, FileWriteMode.APPEND).write(s);
        } catch(IOException e) {
            throw new IllegalStateException("Error writing to file " + this.outputFile.getAbsolutePath(), e);
        }
    }
}
