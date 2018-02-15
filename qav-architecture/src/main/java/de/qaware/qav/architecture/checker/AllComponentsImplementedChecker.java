package de.qaware.qav.architecture.checker;

import de.qaware.qav.architecture.dsl.model.Architecture;
import de.qaware.qav.architecture.dsl.model.Component;
import de.qaware.qav.graph.api.Dependency;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.Node;
import org.slf4j.Logger;

import java.util.Set;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * This class checks an architecture graph that all components are actually implemented. I.e. we don't want to have
 * components which are defined on the architecture level, but don't have corresponding classes in the code base.
 *
 * @author QAware GmbH
 */
public class AllComponentsImplementedChecker extends Checker {

    private static final Logger LOGGER = getLogger(AllComponentsImplementedChecker.class);

    /**
     * Constructor.
     *
     * @param dependencyGraph the dependency graph
     * @param architecture    the architecture to check
     */
    public AllComponentsImplementedChecker(DependencyGraph dependencyGraph, Architecture architecture) {
        super(dependencyGraph, architecture);
        check();
    }

    @Override
    public String getViolationMessage() {
        return getViolationMessages().isEmpty()
                ? null
                : (getViolationMessages().size() + " components without corresponding classes: " + getViolationMessages());
    }

    private void check() {
        architecture.getAllComponents().forEach(this::checkComponent);
    }

    private void checkComponent(Component cmp) {
        Node node = dependencyGraph.getNode(cmp.getName());
        if (node != null) {
            Set<Dependency> edges = dependencyGraph.getBaseGraph().getOutgoingEdges(node, DependencyType.CONTAINS);
            if (edges == null || edges.isEmpty()) {
                LOGGER.warn("{}: Component does not have any corresponding classes.", cmp.getName());
                addViolation(cmp.getName());
            }
        }
    }
}
