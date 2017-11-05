package de.qaware.qav.graph.api;

/**
 * Node filter.
 *
 * @author QAware GmbH
 */
public interface NodeFilter {

    /**
     * return true, if the node is to be kept, and false if not.
     *
     * @param node node
     *
     * @return true if node is wanted, false if not.
     */
    boolean isAccepted(Node node);
}
