package de.qaware.qav.architecture.tagger;

import de.qaware.qav.architecture.dsl.model.Architecture;
import de.qaware.qav.architecture.dsl.model.Component;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.Node;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Tags each architecture component with the "height" of the component.
 *
 * @author QAware GmbH
 */
public final class ArchitectureHeightTagger {

    private static final Logger LOGGER = getLogger(ArchitectureHeightTagger.class);

    /**
     * util class, no instances.
     */
    private ArchitectureHeightTagger() {
    }

    /**
     * Tags each architecture component with the "height" of the component. I.e. it writes a tag on each component node
     * with the name
     * {@literal
     * "<architecture-name>-height"
     * }
     * and the height as value. Architecture components with no children have the height 0, parents have the minimal
     * height of all their children +1.
     *
     * @param dependencyGraph the {@link DependencyGraph}
     * @param architecture    the {@link Architecture}
     */
    public static void tagArchitectureHeight(DependencyGraph dependencyGraph, Architecture architecture) {
        String tagName = architecture.getName() + "-height";

        tagComponentHeight(dependencyGraph, architecture, tagName);
    }

    private static int tagComponentHeight(DependencyGraph dependencyGraph, Component component, String tagName) {
        int height = Integer.MAX_VALUE;

        if (component.getChildren().isEmpty()) {
            height = 0;
        } else {
            int minChildHeight = Integer.MAX_VALUE;
            for (Component child : component.getChildren()) {
                int childHeight = tagComponentHeight(dependencyGraph, child, tagName);
                minChildHeight = Integer.min(childHeight, height);
            }
            height = minChildHeight + 1;
        }

        Node componentNode = dependencyGraph.getNode(component.getName());
        if (componentNode != null) {
            componentNode.setProperty(tagName, height);
        } else {
            LOGGER.warn("No component node for {}", component.getName());
        }
        return height;

    }
}