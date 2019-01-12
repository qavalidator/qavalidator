package de.qaware.qav.architecture.checker;

import de.qaware.qav.architecture.dsl.model.Architecture;
import de.qaware.qav.graph.api.Constants;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.Node;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Checks that all rules which are explicitly defined in an architecture model are actually used in the code base.
 * We don't want to have rules in the architecture model which don't reflect the state of the system.
 *
 * @author QAware GmbH
 */
@Slf4j
public class AllExplicitRulesUsedChecker extends Checker {

    /**
     * Constructor.
     *
     * @param dependencyGraph the architecture graph to check.
     * @param architecture    the {@link Architecture} to use for checking the edges in the architecture graph.
     */
    public AllExplicitRulesUsedChecker(DependencyGraph dependencyGraph, Architecture architecture) {
        super(dependencyGraph, architecture);
        check();
    }

    @Override
    public String getViolationMessage() {
        return getViolationMessages().isEmpty()
                ? null
                : (getViolationMessages().size() + " unused rules: " + getViolationMessages());
    }

    private void check() {
        architecture.getAllComponents().forEach(cmp -> checkUsedRules(cmp.getName()));
    }

    private void checkUsedRules(String componentName) {
        Node node = dependencyGraph.getNode(componentName);
        if (node == null) {
            if (dependencyGraph.getBaseGraph().getNode(componentName) == null) {
                LOGGER.warn("No node for component {}", componentName);
                addViolation("No node for component " + componentName);
            }
            // else: it's ok; there is a node for the component, but it is filtered away for our analysis.
            return;
        }

        List<String> uses = node.getProperty(Constants.USES_API, new ArrayList<>());
        List<String> usedRules = node.getProperty(Constants.USED_RULES, new ArrayList<>());

        uses.stream()
                .filter(it -> !usedRules.contains(it))
                .forEach(it -> {
                    LOGGER.warn("Unused rule in {}: {}", node.getName(), it);
                    addViolation(node.getName() + ": " + it);
                });
    }

}
