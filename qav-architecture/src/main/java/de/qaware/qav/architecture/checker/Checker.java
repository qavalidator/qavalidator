package de.qaware.qav.architecture.checker;

import com.google.common.collect.Lists;
import de.qaware.qav.architecture.dsl.model.Architecture;
import de.qaware.qav.graph.api.DependencyGraph;

import java.util.ArrayList;
import java.util.List;

/**
 * Common base for all architecture checks.
 *
 * @author QAware GmbH
 */
public abstract class Checker {

    private final List<String> violationMessages = Lists.newArrayList();

    protected final DependencyGraph dependencyGraph;
    protected final Architecture architecture;

    /**
     * Constructor.
     *
     * @param dependencyGraph the architecture graph to check.
     * @param architecture    the {@link Architecture} to use.
     */
    public Checker(DependencyGraph dependencyGraph, Architecture architecture) {
        this.dependencyGraph = dependencyGraph;
        this.architecture = architecture;
    }

    /**
     * @return true if the check is fulfilled, false if not.
     */
    public boolean isOk() {
        return violationMessages.isEmpty();
    }

    /**
     * Returns a list of all violation messages.
     *
     * @return a {@link List} of messages
     */
    public List<String> getViolationMessages() {
        return new ArrayList<>(violationMessages);
    }

    /**
     * Returns one message which describes the violations. Null if no violation.
     *
     * @return one message which describes the violations. Null if no violation.
     */
    public abstract String getViolationMessage();

    /**
     * Add a violation message.
     *
     * @param msg the message to add
     */
    protected void addViolation(String msg) {
        this.violationMessages.add(msg);
    }
}
