package de.qaware.qav.architecture.nodecreator;

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

    public static final String DEPENDS_ON_IMPL = "dependsOnImpl";

    /**
     * util class, no instances.
     */
    private DependencyMapper() {
    }

    /**
     * Maps all dependencies given in the Base Graph onto the level of architecture components: For each Edge from V1 to
     * V2, it creates the dependency parent(V1) to parent(V2) in the Target Graph, where the parent nodes are defined by
     * the given tag. They should be set beforehand, see {@link ArchitectureNodeCreator}.
     * We assume that both parent(V1) and parent(V2) exist in the Target Graph.
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

    private static void createArchitectureDependency(DependencyGraph dependencyGraph, String tag, Dependency dep) {
        Node baseFrom = dep.getSource();
        Node baseTo = dep.getTarget();

        // if the tag is not set on either of these two, the Node will be null, too, and no dependency will be mapped:
        Node parentFrom = dependencyGraph.getNode((String) baseFrom.getProperty(tag + Constants.PARENT_SUFFIX));
        Node parentTo = dependencyGraph.getNode((String) baseTo.getProperty(tag + Constants.PARENT_SUFFIX));

        if (parentFrom != null && parentTo != null && !parentFrom.equals(parentTo)) {
            Dependency architectureDependency = dependencyGraph.addDependency(parentFrom, parentTo, dep.getDependencyType());
            architectureDependency.addBaseDependency(dep);

            // the *dependency* goes from component to component.
            // the architecture definition, however, checks if the *API* (or the Impl) may be accessed.
            // So we need to note the *API* name or *Impl* name that this dependency goes to.
            architectureDependency.addListProperty(Constants.TARGET_API, baseTo.getProperty(tag + Constants.PARENT_API_SUFFIX));
            architectureDependency.addListProperty(Constants.TARGET_IMPL, baseTo.getProperty(tag + Constants.PARENT_IMPL_SUFFIX));
        }
    }
}
