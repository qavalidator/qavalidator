package de.qaware.qav.graph.filter;

import de.qaware.qav.graph.api.Node;
import de.qaware.qav.graph.api.NodeFilter;

/**
 * Always returns true.
 *
 * @author QAware GmbH
 */
public class TrueFilter implements NodeFilter {

    @Override
    public boolean isAccepted(Node node) {
        return true;
    }
}
