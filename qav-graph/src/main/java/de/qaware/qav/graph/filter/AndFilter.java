package de.qaware.qav.graph.filter;

import de.qaware.qav.graph.api.Node;
import de.qaware.qav.graph.api.NodeFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Filter which AND-combines a number of filters.
 *
 * @author QAware GmbH
 */
public class AndFilter implements NodeFilter {

    private final List<NodeFilter> baseFilters = new ArrayList<>();

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
        return baseFilters.stream().allMatch(filter -> filter.isAccepted(node));
    }
}
