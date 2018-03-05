package de.qaware.qav.architecture.viewcreator;

import de.qaware.qav.architecture.viewcreator.impl.ArchitectureNodeCreator;
import de.qaware.qav.architecture.viewcreator.impl.ComponentNameTagger;
import de.qaware.qav.graph.api.Constants;
import de.qaware.qav.graph.api.Dependency;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.Node;

/**
 * Maps all dependencies given in the Base Graph onto the architecture level.
 *
 * @author QAware GmbH
 */
public final class DependencyMapper {

    /**
     * util class, no instances.
     */
    private DependencyMapper() {
    }

    /**
     * Maps all dependencies in the given graph onto the level of architecture components:
     * <p>
     * For each Edge from V1 to V2, it creates the dependency parent(V1) to parent(V2) in the Target Graph, where the
     * parent nodes are defined by the given tag. They should be set beforehand, see {@link
     * ComponentNameTagger} and {@link ArchitectureNodeCreator}.
     * We assume that both parent(V1) and parent(V2) exist in the Base Graph.
     *
     * @param dependencyGraph the {@link DependencyGraph}
     * @param tag             the name of the architecture
     */
    public static void mapDependencies(DependencyGraph dependencyGraph, String tag) {
        dependencyGraph.getAllEdges()
                .stream()
                .filter(dep -> dep.getDependencyType() != DependencyType.CONTAINS)
                .forEach(edge -> createArchitectureDependency(dependencyGraph.getBaseGraph(), tag, edge));
    }

    private static void createArchitectureDependency(DependencyGraph baseGraph, String tag, Dependency dep) {
        Node from = dep.getSource();
        Node to = dep.getTarget();

        // if the tag is not set on either of these two, the Node will be null, too, and no dependency will be mapped:
        Node parentFrom = baseGraph.getNode((String) from.getProperty(tag + Constants.PARENT_SUFFIX));
        Node parentTo = baseGraph.getNode((String) to.getProperty(tag + Constants.PARENT_SUFFIX));

        if (parentFrom != null && parentTo != null && !parentFrom.equals(parentTo)) {
            Dependency architectureDependency = baseGraph.addDependency(parentFrom, parentTo, dep.getDependencyType());
            architectureDependency.addBaseDependency(dep);

            // the *dependency* goes from component to component.
            // the architecture definition, however, checks if the *API* (or the Impl) may be accessed.
            // So we need to note the *API* name or *Impl* name that this dependency goes to.
            architectureDependency.addListProperty(Constants.TARGET_API, to.getProperty(tag + Constants.PARENT_API_SUFFIX));
            architectureDependency.addListProperty(Constants.TARGET_IMPL, to.getProperty(tag + Constants.PARENT_IMPL_SUFFIX));
        }
    }
}
