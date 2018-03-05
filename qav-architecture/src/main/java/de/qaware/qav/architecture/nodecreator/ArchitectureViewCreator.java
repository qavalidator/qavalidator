package de.qaware.qav.architecture.nodecreator;

import de.qaware.qav.architecture.dsl.model.Architecture;
import de.qaware.qav.architecture.nodecreator.impl.ArchitectureNodeCreator;
import de.qaware.qav.architecture.nodecreator.impl.ArchitectureNodeTagger;
import de.qaware.qav.architecture.nodecreator.impl.BaseRelationTagger;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.filter.AndFilter;
import de.qaware.qav.graph.filter.NodePropertyInFilter;

import java.util.List;

/**
 * Create an architecture view.
 * <p>
 * An <i>architecture view</i> is a {@link DependencyGraph} which contains only the nodes representing the {@link
 * Architecture}, and which contains all dependencies of the underlying graph, mapped up to the architecture nodes.
 *
 * @author QAware GmbH
 */
public final class ArchitectureViewCreator {

    private ArchitectureViewCreator() {
    }

    /**
     * Creates a new architecture view in the given {@link DependencyGraph}.
     * <p>
     * The result is a filtered graph which contains all nodes which belong to this architecture view (i.e. filtered
     * according to the given architecture).
     *
     * @param sourceGraph  the graph from which we create the new architecture view.
     * @param architecture the {@link Architecture} which defines the view.
     * @param tag          the tag to write on every node on the given sourceGraph and on each parent architecture node;
     *                     if this is null, the tag defaults to the architecture name.
     * @return a {@link Result} object which carries the architecture graph and possibly a violation message, if not all
     * classes could be mapped to an architecture component.
     */
    public static Result createArchitectureView(DependencyGraph sourceGraph, Architecture architecture, String tag) {
        if (tag == null) {
            tag = architecture.getName();
        }

        Result result = new Result();

        List<String> unmappedClasses = new ArchitectureNodeCreator(sourceGraph, architecture).createAllArchitectureNodes();
        if (!unmappedClasses.isEmpty()) {
            result.setViolationMessage(String.format("There are unmapped classes in architecture %s: %s", architecture.getName(), unmappedClasses));
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
