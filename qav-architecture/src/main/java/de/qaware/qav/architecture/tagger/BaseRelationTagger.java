package de.qaware.qav.architecture.tagger;

import de.qaware.qav.graph.api.Constants;
import de.qaware.qav.graph.api.Dependency;
import de.qaware.qav.graph.api.DependencyGraph;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Tags each architecture dependency with the number of base relations, the number of different source nodes in
 * ths base relations, and the number of different target nodes in the base relations.
 *
 * @author QAware GmbH
 */
public final class BaseRelationTagger {

    /**
     * util class, no instances.
     */
    private BaseRelationTagger() {
    }

    /**
     * Tags each relation in the given graph with the number of base relations, the number of different source nodes in
     * the base relations, and the number of different target nodes in the base relations.
     *
     * @param graph the {@link DependencyGraph}
     */
    public static void tagBaseRelationNumbers(DependencyGraph graph) {
        graph.getAllEdges().forEach(BaseRelationTagger::tagBaseRelationNumbers);
    }

    private static void tagBaseRelationNumbers(Dependency dependency) {
        Set<Dependency> baseDependencies = dependency.getBaseDependencies();

        if (!baseDependencies.isEmpty()) {
            Set<String> sourceNames = baseDependencies.stream()
                    .map(dep -> dep.getSource().getName())
                    .collect(Collectors.toSet());

            Set<String> targetNames = baseDependencies.stream()
                    .map(dep -> dep.getTarget().getName())
                    .collect(Collectors.toSet());

            dependency.setProperty(Constants.BASE_REL_COUNT, baseDependencies.size());
            dependency.setProperty(Constants.BASE_REL_COUNT_SOURCES, sourceNames.size());
            dependency.setProperty(Constants.BASE_REL_COUNT_TARGETS, targetNames.size());
        }
    }
}
