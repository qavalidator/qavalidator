package de.qaware.qav.graph.alg.api;

import de.qaware.qav.graph.api.Node;

import java.util.List;

/**
 * Detect cycles.
 *
 * @author QAware GmbH
 */
@SuppressWarnings("squid:S1214")
// does not recommend commend constants in interfaces. But here, using this property name is part of the interface.
public interface CycleFinder {

    /**
     * property name for detected cycle: attribute is a boolean.
     */
    String IN_CYCLE = "IN_CYCLE";

    /**
     * property name for the cycle label: attribute is a label identifying the cycle.
     */
    String CYCLE_LABEL = "CYCLE";

    /**
     * returns <tt>true</tt> if there is a cycle, <tt>false</tt> if not.
     *
     * @return <tt>true</tt> if there is a cycle, <tt>false</tt> if not.
     */
    boolean hasCycles();

    /**
     * gets the nodes which are part of the cycle.
     *
     * @return the nodes which are part of the cycle.
     */
    List<List<Node>> getCycles();
}
