package de.qaware.qav.graph.filter;

import com.google.common.collect.Lists;
import de.qaware.qav.graph.api.Node;
import de.qaware.qav.graph.api.NodeFilter;

import java.util.Arrays;
import java.util.List;

/**
 * Filter which AND-combines a number of filters.
 *
 * @author QAware GmbH
 */
public class AndFilter implements NodeFilter {

    private final List<NodeFilter> baseFilters = Lists.newArrayList();

    /**
     * Value constructor.
     *
     * @param baseFilters the {@link NodeFilter}s; accepts the node if ALL of them accept it
     */
    public AndFilter(NodeFilter... baseFilters) {
        this.baseFilters.addAll(Arrays.asList(baseFilters));
    }

    @Override
    public boolean isAccepted(Node node) {
        for (NodeFilter filter : baseFilters) {
            if (!filter.isAccepted(node)) {
                return false;
            }
        }

        return true;
    }
}
