package de.qaware.qav.architecture.tagger;

import de.qaware.qav.architecture.dsl.model.Architecture;
import de.qaware.qav.architecture.dsl.model.Component;
import de.qaware.qav.graph.api.Constants;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.Node;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Tags each node with the name of the architecture component it belongs to.
 * Also tags it with "ignored" flags and "implementation" flags.
 * <p>
 * Checks if each node is assigned to a component or not.
 *
 * @author QAware GmbH
 */
public final class ComponentNameTagger {

    private static final Logger LOGGER = getLogger(ComponentNameTagger.class);

    /**
     * util class, no instances.
     */
    private ComponentNameTagger() {
    }

    /**
     * Creates a tag on each node in the given graph to tell the name of the architecture component it belongs to.
     * Also writes a tag to indicate whether it is part of the implementation (as opposed to the API).
     *
     * @param dependencyGraph the {@link DependencyGraph}
     * @param architecture    the {@link Architecture}
     * @return the list of unmapped classes. Never null. If empty, all classes the given graph could be mapped.
     */
    public static List<String> tagComponentNames(DependencyGraph dependencyGraph, Architecture architecture) {
        final List<String> unmappedClasses = new ArrayList<>();

        dependencyGraph.getAllNodes().forEach(node -> {
            if (!tagComponentName(node, architecture)) {
                unmappedClasses.add(node.getName());
            }
        });

        return unmappedClasses;
    }

    /**
     * Sets properties regarding the architecture on the give node.
     *
     * @param node         the {@link Node} to set properties on
     * @param architecture the {@link Architecture}
     * @return <tt>true</tt> if the {@link Node} could be mapped, i.e. was found in the {@link Architecture},
     * <tt>false</tt> if the n{@link Node} is unmapped.
     */
    private static boolean tagComponentName(Node node, Architecture architecture) {

        final String name = architecture.getName();

        boolean isMapped = true;

        if (!architecture.isIncluded(node.getName())) {
            node.setProperty(name + Constants.IGNORED_SUFFIX, true);
        } else {
            Component component = architecture.getParentComponent(node.getName());
            if (component != null) {
                node.setProperty(name + Constants.PARENT_SUFFIX, component.getName());
                node.setProperty(name + Constants.PARENT_API_SUFFIX, component.getApiName(node.getName()));
                boolean isImpl = node.getProperty(name + Constants.IMPL_SUFFIX, false)
                        || component.isImpl(node.getName());
                node.setProperty(name + Constants.IMPL_SUFFIX, isImpl);
            } else {
                LOGGER.warn("Node {} is not mapped to any component", node.getName());
                node.setProperty(name + Constants.UNMAPPED_SUFFIX, true);
                node.addListProperty(Constants.UNMAPPED, name);
                isMapped = false;
            }
        }

        return isMapped;
    }
}
