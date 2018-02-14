package de.qaware.qav.architecture.checker;

import com.google.common.collect.Lists;
import de.qaware.qav.architecture.dsl.model.Architecture;
import de.qaware.qav.graph.api.Constants;
import de.qaware.qav.graph.api.Dependency;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.Node;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * This class checks an architecture graph that all edges are allowed, i.e. that for each edge there is at least one
 * rule which justifies the existence of that edge.
 *
 * @author QAware GmbH
 */
public class DependencyChecker extends Checker {

    private static final Logger LOGGER = getLogger(DependencyChecker.class);

    private final List<Dependency> violatingDependencies = Lists.newArrayList();

    /**
     * Constructor.
     *
     * @param dependencyGraph the architecture graph to check.
     * @param architecture    the {@link Architecture} to use for checking the edges in the architecture graph.
     */
    public DependencyChecker(DependencyGraph dependencyGraph, Architecture architecture) {
        super(dependencyGraph, architecture);
        check();
    }

    @Override
    public String getViolationMessage() {
        return getViolationMessages().isEmpty()
                ? null
                : getViolationMessages().size() + " uncovered dependencies: " + getViolationMessages();
    }

    private void check() {
        dependencyGraph.getAllEdges().stream()
                .filter(edge -> edge.getDependencyType() != DependencyType.CONTAINS)
                .filter(edge -> !hasMatchingRule(edge))
                .forEach(violatingDependencies::add);

        logViolatingDependencies();
    }

    /**
     * Checks if there is a rule which allows the given edge.
     *
     * I.e., it checks all actual references of the given dependency against "uses" and "usesImpl" rules.
     * <p>
     * Note that for each dependency (i.e.: edge in the graph), there may be more than one reference to APIs oder Impl
     * parts of the target component. Therefore, we need to check each access to each API or Impl.
     * <p>
     * They are annotated in the Graph:
     * the actual references are annotated as properties on the edge;
     * the allowed references are annotated as properties on the source node.
     *
     * @param edge the edge to check
     * @return true if there is at least one rule which allows all references of the given edge.
     */
    private boolean hasMatchingRule(Dependency edge) {
        LOGGER.debug("Checking actual references against all uses and usesImpl rules: {}", edge);
        return
                (isInSameScope(edge) || checkAllReferencesAllowed(edge, Constants.TARGET_API, Constants.USES_API))
                        && checkAllReferencesAllowed(edge, Constants.TARGET_IMPL, Constants.USES_IMPL);
    }

    @SuppressWarnings("squid:S1698") // compare with "==" instead of .equals()
    private boolean isInSameScope(Dependency edge) {
        LOGGER.debug("Checking for same namespace access: {}", edge);
        Node source = edge.getSource();
        String scopeName = architecture.getParentComponentName(edge.getTarget().getName());
        if (scopeName == null) {
            // this may happen if 'target' is at the root of the architecture tree.
            // Then we just check if the 'source' is is a (grand-) child of 'target'.
            scopeName = edge.getTarget().getName();
        }
        Node scope = dependencyGraph.getNode(scopeName);
        if (scope == null) {
            throw new IllegalStateException("Scope " + scopeName + " has no corresponding node in the dependency graph.");
        }

        while (source != null) {
            if (source == scope) { // yes, compare íf it's the same instance.
                LOGGER.debug("Same namespace for {}: true", edge);
                return true;
            }
            source = getParentNode(source);
        }
        LOGGER.debug("Same namespace for {}: false", edge);
        return false;
    }

    /**
     * Checks if the dependency is allowed by this component or a parent component.
     *
     * @param edge                 the edge to check
     * @param actualReferencesKey  the key to look up the actual references; for impl: {@link Constants#USES_IMPL},
     *                             for API: {@link Constants#USES_API}
     * @param allowedReferencesKey the key to look up the allowed references; for impl: {@link Constants#TARGET_IMPL},
     *                             for API: {@link Constants#TARGET_API}
     * @return true if a rule could be found which justifies all references in that dependency
     */
    private boolean checkAllReferencesAllowed(Dependency edge, String actualReferencesKey, String allowedReferencesKey) {
        List<String> usedReferences = edge.getProperty(actualReferencesKey, new ArrayList<String>());
        LOGGER.debug("Checking rules for edge: {} and references {}", edge, usedReferences);
        for (String usedRef : usedReferences) {
            if (!checkReferenceAllowed(edge.getSource(), usedRef, allowedReferencesKey)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if the reference is allowed by a "uses" or "usesImpl" rule in this component or a parent component.
     *
     * @param source               the outgoing node
     * @param usedRef              the name of the referenced API or Impl
     * @param allowedReferencesKey the key to look up the allowed references; for impl: {@link Constants#TARGET_IMPL},
     *                             for API: {@link Constants#TARGET_API}
     * @return true if a rule could be found which justifies the reference
     */
    private boolean checkReferenceAllowed(Node source, String usedRef, String allowedReferencesKey) {
        ArrayList<String> allowedRefs = source.getProperty(allowedReferencesKey, new ArrayList<String>());
        LOGGER.debug("Checking reference: Node {}, outgoing reference: {}, allowedReferences: {}", source.getName(), usedRef, allowedRefs);
        if (allowedRefs.contains(usedRef)) {
            source.addListProperty(Constants.USED_RULES, usedRef);
            return true;
        }

        Node parentNode = getParentNode(source);
        return parentNode != null && checkReferenceAllowed(parentNode, usedRef, allowedReferencesKey);
    }

    private Node getParentNode(Node source) {
        return dependencyGraph.getNode(architecture.getParentComponentName(source.getName()));
    }

    private void logViolatingDependencies() {
        if (!violatingDependencies.isEmpty()) {
            LOGGER.error("There are {} uncovered dependencies: ", violatingDependencies.size());
            violatingDependencies.forEach(dep -> {
                        LOGGER.error("  {}", dep);
                        dep.getBaseDependencies().forEach(baseDep ->
                                LOGGER.error("      * {}", baseDep));
                        addViolation(dep.toString());
                    }
            );
        }
    }

}
