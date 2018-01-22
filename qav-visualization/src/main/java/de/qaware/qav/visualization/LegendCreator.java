package de.qaware.qav.visualization;

import de.qaware.qav.architecture.dsl.model.Architecture;
import de.qaware.qav.architecture.factory.DefaultPackageArchitectureFactory;
import de.qaware.qav.architecture.nodecreator.ArchitectureNodeCreator;
import de.qaware.qav.architecture.nodecreator.DependencyMapper;
import de.qaware.qav.architecture.tagger.BaseRelationTagger;
import de.qaware.qav.graph.api.Constants;
import de.qaware.qav.graph.api.Dependency;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.Node;
import de.qaware.qav.graph.factory.DependencyGraphFactory;

import java.util.ArrayList;

/**
 * Creates a graph which it then uses to export via Dot/GraphML to draw a legend.
 *
 * @author QAware GmbH
 */
public class LegendCreator {

    private DependencyGraph graph = DependencyGraphFactory.createGraph();
    private Architecture architecture;

    /**
     * Constructor.
     */
    public LegendCreator() {
        init();
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

    private void init() {
        // set up the graph: one pair of node for each dependency type
        for (int i = 0; i < DependencyType.values().length; i++) {
            DependencyType type = DependencyType.values()[i];
            // skip CONTAINS, as no edge is drawn, instead the nodes are nested.
            if (type != DependencyType.CONTAINS) {
                addDependency(type);
            }
        }

        // set up the architecture:
        this.architecture = new DefaultPackageArchitectureFactory(this.graph).createArchitecture();
        ArchitectureNodeCreator.createAllArchitectureNodes(this.graph, architecture);
        DependencyMapper.mapDependencies(this.graph, architecture.getName());
        BaseRelationTagger.tagBaseRelationNumbers(this.graph);
    }

    private void addDependency(DependencyType type) {
        Node a = graph.getOrCreateNodeByName("a" + type.ordinal());
        Node b = graph.getOrCreateNodeByName("b" + type.ordinal());

        Dependency dependency = graph.addDependency(a, b, type);
        // this is a bit of a hack: usually, BASE_REL_COUNT is a number. But the exporter just reads it as an Object.
        dependency.setProperty(Constants.BASE_REL_COUNT, type.name());
    }
}
