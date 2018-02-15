package de.qaware.qav.server.mapper;

import com.google.common.collect.Maps;
import de.qaware.qav.graph.api.Dependency;
import de.qaware.qav.server.model.DependencyDTO;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Mapper from {@link Dependency} to {@link DependencyDTO}.
 *
 * @author QAware GmbH
 */
public final class DependencyMapper {

    /**
     * util class, no instances
     */
    private DependencyMapper() {
    }

    /**
     * Map a {@link Dependency} to {@link DependencyDTO}.
     *
     * @param dependency the {@link Dependency}
     * @return the {@link DependencyDTO}
     */
    public static DependencyDTO toDTO(Dependency dependency) {
        checkNotNull(dependency, "dependency");

        DependencyDTO result = new DependencyDTO();
        result.setSourceName(dependency.getSource().getName());
        result.setTargetName(dependency.getTarget().getName());
        result.setTypeName(dependency.getDependencyType().name());

        result.setBaseDependencies(mapBaseDependencies(dependency.getBaseDependencies()));
        result.setProperties(mapProperties(dependency.getProperties()));

        return result;
    }

    private static List<DependencyDTO> mapBaseDependencies(Set<Dependency> baseDependencies) {
        return baseDependencies.stream()
                .map(DependencyMapper::toDTO)
                .sorted(Comparator.comparing(DependencyDTO::getSourceName))
                .collect(Collectors.toList());
    }

    private static Map<String, Object> mapProperties(Map<String, Object> properties) {
        Map<String, Object> result = Maps.newTreeMap();
        result.putAll(properties);
        return result;
    }
}
