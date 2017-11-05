package de.qaware.qav.graph.factory;

import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.impl.DependencyGraphSimpleImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory for {@link DependencyGraph} instances.
 *
 * @author QAware GmbH
 */
public final class DependencyGraphFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(DependencyGraphFactory.class);

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
        LOGGER.debug("Creating graph");

        return new DependencyGraphSimpleImpl();
    }
}
