package de.qaware.qav.architecture.nodecreator;

import de.qaware.qav.architecture.dsl.model.Architecture;
import de.qaware.qav.architecture.tagger.BaseRelationTagger;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.filter.AndFilter;
import de.qaware.qav.graph.filter.NodePropertyInFilter;

import java.util.List;

/**
 * Create an architecture view.
 *
 * @author QAware GmbH
 */
public final class ArchitectureViewCreator {

    private ArchitectureViewCreator() {
    }

    /**
     * Creates a new architecture view in the given {@link DependencyGraph}.
     *
     * The result is a filtered graph which contains all nodes which belong to this architecture view (i.e. filtered
     * according to the given architecture).
     *
     * @param sourceGraph the graph from which we create the new architecture view.
     * @param architecture the {@link Architecture} which defines the view.
     * @param tag the tag to write on every node on the given sourceGraph and on each parent architecture node;
     *            if this is null or not given, the tag defaults to the architecture name.
     * @return a {@link Result} object which carries the architecture graph and possibly a violation message, if not all
     *         classes could be mapped to an architecture component.
     */
    public static Result createArchitectureView(DependencyGraph sourceGraph, Architecture architecture, String tag) {
        if (tag == null) {
            tag = architecture.getName();
        }

        Result result = new Result();

        List<String> unmappedClasses = ArchitectureNodeCreator.createAllArchitectureNodes(sourceGraph, architecture);
        if (!unmappedClasses.isEmpty()) {
            String violationMessage = "There are unmapped classes in architecture " + architecture.getName() + ": " + unmappedClasses;
            result.setViolationMessage(violationMessage);
        }

        DependencyMapper.mapDependencies(sourceGraph, architecture.getName());
        ArchitectureNodeTagger.tagArchitectureNodes(sourceGraph, architecture, tag);

        DependencyGraph architectureGraph = sourceGraph.getBaseGraph().filter(
                new AndFilter(
                        new NodePropertyInFilter(tag, true),
                        new NodePropertyInFilter("type", "architecture")
                ));

        BaseRelationTagger.tagBaseRelationNumbers(architectureGraph);

        result.setArchitectureGraph(architectureGraph);
        return result;
    }
}
