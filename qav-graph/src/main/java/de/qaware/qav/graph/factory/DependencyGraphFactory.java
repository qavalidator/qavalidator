package de.qaware.qav.graph.factory;

import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.impl.DependencyGraphSimpleImpl;
import lombok.extern.slf4j.Slf4j;

/**
 * Factory for {@link DependencyGraph} instances.
 *
 * @author QAware GmbH
 */
@Slf4j
public final class DependencyGraphFactory {

    /**
     * Factory, no instances.
     */
    private DependencyGraphFactory() {
    }

    /**
     * create a new {@link DependencyGraph}
     *
     * @return a new {@link DependencyGraph}
     */
    public static DependencyGraph createGraph() {
        LOGGER.debug("Creating graph: {}", DependencyGraphSimpleImpl.class.getName());

        return new DependencyGraphSimpleImpl();
    }
}
