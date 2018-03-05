package de.qaware.qav.visualization;

import de.qaware.qav.architecture.dsl.model.Architecture;
import de.qaware.qav.graph.api.Constants;
import de.qaware.qav.graph.api.Dependency;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.Node;
import de.qaware.qav.graph.factory.DependencyGraphFactory;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Creates a graph which it then uses to export via Dot/GraphML to draw a legend.
 *
 * @author QAware GmbH
 */
public class LegendCreator {

    private final DependencyGraph graph = DependencyGraphFactory.createGraph();
    private final Architecture architecture = new Architecture();

    /**
     * Constructor.
     */
    public LegendCreator() {
        initGraph();
    }

    /**
     * Export the sample graph to create the legend.
     *
     * @param filenameBase the base name; will be enhanced with the correct file endings
     */
    public void export(String filenameBase) {
        new DotExporter(this.graph, filenameBase, this.architecture, new ArrayList<>(), true).exportGraph();
        new GraphMLExporter(this.graph, filenameBase, this.architecture, new ArrayList<>(), true).exportGraph();
    }

    /**
     * Set up the graph: one pair of node for each dependency type.
     * <p>
     * Skip CONTAINS, as no edge is drawn, instead the nodes are nested.
     */
    private void initGraph() {
        Arrays.stream(DependencyType.values())
                .filter(type -> type != DependencyType.CONTAINS)
                .forEach(this::addDependency);
    }

    private void addDependency(DependencyType type) {
        Node a = graph.getOrCreateNodeByName("a" + type.ordinal());
        Node b = graph.getOrCreateNodeByName("b" + type.ordinal());

        Dependency dependency = graph.addDependency(a, b, type);
        // this is a bit of a hack: usually, BASE_REL_COUNT is a number. But the exporter just reads it as an Object.
        dependency.setProperty(Constants.BASE_REL_COUNT, type.name());
    }
}
