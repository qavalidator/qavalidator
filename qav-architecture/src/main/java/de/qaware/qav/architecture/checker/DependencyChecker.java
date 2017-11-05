package de.qaware.qav.architecture.checker;

import com.google.common.collect.Lists;
import de.qaware.qav.architecture.dsl.model.Architecture;
import de.qaware.qav.architecture.nodecreator.DependencyMapper;
import de.qaware.qav.graph.api.Constants;
import de.qaware.qav.graph.api.Dependency;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.Node;
import org.slf4j.Logger;

import java.util.List;
import java.util.Set;

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
    private final String implPropertyKey;

    /**
     * Constructor.
     *
     * @param dependencyGraph the architecture graph to check.
     * @param architecture    the {@link Architecture} to use for checking the edges in the architecture graph.
     */
    public DependencyChecker(DependencyGraph dependencyGraph, Architecture architecture) {
        super(dependencyGraph, architecture);
        implPropertyKey = architecture.getName() + Constants.IMPL_SUFFIX;
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
     * <p>
     * Checks for dependencies on implementation; logs all dependencies on implementation, including the relevant
     * base relations.
     *
     * @param edge the edge
     * @return true if there is at least one rule which allows the given edge.
     */
    private boolean hasMatchingRule(Dependency edge) {
        Boolean dependsOnImpl = edge.getProperty(DependencyMapper.DEPENDS_ON_IMPL, false);
        if (dependsOnImpl) {
            LOGGER.warn("Dependency on implementation instead of API: {}:", edge);
            edge.getBaseDependencies().stream()
                    .filter(baseDep -> baseDep.getTarget().getProperty(implPropertyKey, false))
                    .forEach(baseDep -> LOGGER.warn("      * {}", baseDep));
        }

        return isInSameScope(edge) || hasAllowedParentEdge(edge);
    }

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

    private boolean hasAllowedParentEdge(Dependency edge) {
        Node source = edge.getSource();
        Node target = edge.getTarget();
        Node targetParent = getParentNode(target);
        while (source != null && targetParent != null) {
            // also check relation to the parent because allowed dependencies are declared on the components, not on the leafs.
            LOGGER.debug("Checking: {} -> {} and {} -> {}", source.getName(), target.getName(), source.getName(), targetParent.getName());
            if (isExplicitlyAllowed(source, target) || isExplicitlyAllowed(source, targetParent)) {
                return true;
            }
            source = getParentNode(source);
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private Boolean isExplicitlyAllowed(Node source, Node target) {
        List<String> uses = (List<String>) source.getProperty(Constants.USES);

        Boolean result = uses != null && uses.contains(target.getName());
        if (result) {
            source.addListProperty(Constants.USED_RULES, target.getName());
        }

        Dependency edge = dependencyGraph.getEdge(source, target);
        if (edge != null) {
            List<String> referencedApis = (List<String>) edge.getProperty(Constants.TARGET_API);
            if (uses != null && referencedApis != null && uses.containsAll(referencedApis)) {
                referencedApis.forEach(targetName -> source.addListProperty(Constants.USED_RULES, targetName));
                result = true;
            }
        }

        return result;
    }

    private Node getParentNode(Node source) {
        Set<Dependency> incomingEdges = dependencyGraph.getIncomingEdges(source, DependencyType.CONTAINS);
        if (!incomingEdges.isEmpty()) {
            if (incomingEdges.size() > 1) {
                LOGGER.error("Node {} has more than one parent: {}", source.getName(), incomingEdges);
            }
            return incomingEdges.iterator().next().getSource();
        }
        return null;
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
