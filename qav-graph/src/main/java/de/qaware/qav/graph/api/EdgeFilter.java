package de.qaware.qav.graph.api;

/**
 * Edge filter.
 *
 * @author QAware GmbH
 */
public interface EdgeFilter {

    /**
     * return true, if the edge is to be kept, and false if not.
     *
     * @param edge edge
     *
     * @return true if edge is wanted, false if not.
     */
    boolean isAccepted(Dependency edge);
}
