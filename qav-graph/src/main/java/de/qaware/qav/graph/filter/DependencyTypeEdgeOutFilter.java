package de.qaware.qav.graph.filter;

import com.google.common.collect.Lists;
import de.qaware.qav.graph.api.Dependency;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.EdgeFilter;

import java.util.Arrays;
import java.util.List;

/**
 * Accepts edges which do NOT have one of the given types.
 *
 * @author QAware GmbH
 */
public class DependencyTypeEdgeOutFilter implements EdgeFilter {

    private final List<DependencyType> unwantedTypes = Lists.newArrayList();

    /**
     * Constructor.
     *
     * @param unwantedTypes {@link DependencyType}s to be excluded
     */
    public DependencyTypeEdgeOutFilter(DependencyType... unwantedTypes) {
        this.unwantedTypes.addAll(Arrays.asList(unwantedTypes));
    }

    @Override
    public boolean isAccepted(Dependency edge) {
        for (DependencyType dependencyType : unwantedTypes) {
            if (edge.getDependencyType().equals(dependencyType)) {
                return false;
            }
        }
        return true;
    }
}
