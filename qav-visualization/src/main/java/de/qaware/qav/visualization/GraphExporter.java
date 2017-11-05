package de.qaware.qav.visualization;

import de.qaware.qav.architecture.dsl.model.Architecture;
import de.qaware.qav.graph.api.DependencyGraph;

import java.util.List;

/**
 * Export a {@link DependencyGraph} to DOT and GraphML.
 *
 * @author QAware GmbH
 */
public final class GraphExporter {

    /**
     * util class without instance.
     */
    private GraphExporter() {
    }

    /**
     * Convenience method: Initializes the {@link DotExporter} and {@link GraphMLExporter} with the given parameters,
     * and exports the graph.
     *
     * @param dependencyGraph   the {@link DependencyGraph} to export
     * @param fileNameBase      the file name base, used to set up the .dot, .png, and .graphml file name
     * @param architecture      the {@link Architecture} to use for clusters (nested nodes)
     * @param abbreviationsList the list of {@link Abbreviation}s, to create labels
     * @param createEdgeLabels  true to print edge labels, false to omit them
     */
    public static void export(DependencyGraph dependencyGraph, String fileNameBase, Architecture architecture,
                              List<Abbreviation> abbreviationsList,
                              boolean createEdgeLabels) {
        new DotExporter(dependencyGraph, fileNameBase, architecture, abbreviationsList, createEdgeLabels).exportGraph();
        new GraphMLExporter(dependencyGraph, fileNameBase, architecture, abbreviationsList, createEdgeLabels).exportGraph();
    }

}
