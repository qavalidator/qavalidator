package de.qaware.qav.architecture.checker;

import de.qaware.qav.architecture.dsl.model.Architecture;
import de.qaware.qav.graph.api.Constants;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.Node;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Checks that all rules which are explicitly defined in an architecture model are actually used in the code base.
 * We don't want to have rules in the architecture model which don't reflect the state of the system.
 *
 * @author QAware GmbH
 */
public class AllExplicitRulesUsedChecker extends Checker {

    private static final Logger LOGGER = getLogger(AllExplicitRulesUsedChecker.class);

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
                : getViolationMessages().size() + " unused rules: " + getViolationMessages();
    }

    private void check() {
        architecture.getAllComponents().forEach(cmp -> checkUsedRules(cmp.getName()));
    }

    @SuppressWarnings("unchecked")
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

        List<String> uses = node.getProperty(Constants.USES, new ArrayList<String>());
        List<String> usedRules = node.getProperty(Constants.USED_RULES, new ArrayList<String>());

        uses.stream()
                .filter(it -> !usedRules.contains(it))
                .forEach(it -> {
                    LOGGER.warn("Unused rule in {}: {}", node.getName(), it);
                    addViolation(node.getName() + ": " + it);
                });
    }

}
