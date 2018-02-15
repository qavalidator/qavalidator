package de.qaware.qav.architecture.factory;

import com.google.common.collect.Lists;
import de.qaware.qav.architecture.dsl.model.Architecture;
import de.qaware.qav.architecture.dsl.model.ClassSet;
import de.qaware.qav.architecture.dsl.model.Component;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.Node;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Create an {@link Architecture} from a given
 * {@link DependencyGraph}.
 *
 * @author QAware GmbH
 */
public final class DefaultPackageArchitectureFactory {

    private static final Logger LOGGER = getLogger(DefaultPackageArchitectureFactory.class);

    public static final String ROOT_NAME = "Package";

    private final DependencyGraph dependencyGraph;
    private String architectureName = ROOT_NAME;
    private String pathSeparator = ".";
    private String pathSplitterRegExp = "\\.";

    /**
     * Constructor.
     *
     * @param dependencyGraph the {@link DependencyGraph}, filtered appropriately
     */
    public DefaultPackageArchitectureFactory(DependencyGraph dependencyGraph) {
        this.dependencyGraph = checkNotNull(dependencyGraph, "dependencyGraph");
    }

    public void setArchitectureName(String architectureName) {
        this.architectureName = architectureName;
    }

    /**
     * Setter. This regular expression is used to split the names to find hierarchical structures.
     * Defaults to the dot, which is useful for fully qualified Java class names, or also for property names.
     *
     * If the separator is the dot, then escape it, because the splitter works on regular expressions.
     *
     * @param separator the separator String used to split and join the parts
     */
    public void setPathSeparator(String separator) {
        this.pathSeparator = separator;
        if (".".equals(separator)) {
            this.pathSplitterRegExp = "\\.";
        } else {
            this.pathSplitterRegExp = separator;
        }
    }

    /**
     * Creates a new {@link Architecture} for the nodes in the given graph. Uses a plain package architecture, i.e. it
     * produces a "bunch-of-classes-architecture", based on the package names.
     * <p>
     * All classes are considered "API" in that package.
     *
     * @return the new {@link Architecture}
     */
    public Architecture createArchitecture() {
        return createArchitecture(0);
    }

    /**
     * Creates a new {@link Architecture} for the nodes in the given graph. Uses a plain package architecture, i.e. it
     * produces a "bunch-of-classes-architecture", based on the package names.
     * <p>
     * All classes are considered "API" in that package.
     *
     * @param maxLength max length of the package name depth
     * @return the new {@link Architecture}
     */
    public Architecture createArchitecture(int maxLength) {
        checkNotNull(dependencyGraph, "graph may not be null");

        Architecture result = new Architecture();
        result.setName(maxLength == 0 ? architectureName : (architectureName + "-" + maxLength));

        for (Node node : dependencyGraph.getAllNodes()) {
            String name = node.getName();
            createComponent(result, name, maxLength);
        }

        for (Node node : dependencyGraph.getAllNodes()) {
            String componentName = getComponentName(node.getName(), maxLength);
            if (componentName == null) {
                LOGGER.debug("Node {}: is a direct child of Architecture Root Node, creating component explicitly", node.getName());
                Component component = doCreateComponent(node.getName());
                component.setParent(result);
                result.getChildren().add(component);
                result.getNameToComponent().put(node.getName(), component);
            }
        }

        result.getParentComponent("fs");
        // Sort according to the length of the names (= names of the include patterns) so that the most detailed
        // hit matches: make sure to be correct with nested components.
        // If the length is equal, sort according to the name to be predictive.
        result.getAllComponents().sort((c1, c2) -> {
            int cc = c2.getName().length() - c1.getName().length();
            return cc == 0 ? c1.getName().compareTo(c2.getName()) : cc;
        });

        return result;
    }

    private Component createComponent(Architecture architecture, String name, int maxLength) {
        String componentName = getComponentName(name, maxLength);
        if (componentName == null) {
            return architecture;
        }

        Component component = architecture.getNameToComponent().get(componentName);
        if (component == null) {
            component = doCreateComponent(componentName);
            architecture.getAllComponents().add(component);
            architecture.getNameToComponent().put(componentName, component);
            architecture.getApiNameToComponent().put(componentName, component);

            Component parent = createComponent(architecture, componentName, maxLength);
            component.setParent(parent);
            parent.getChildren().add(component);
        }

        return component;
    }

    private Component doCreateComponent(String componentName) {
        LOGGER.debug("Creating Component {}", componentName);
        Component result = new Component();
        result.setName(componentName);
        ClassSet apiDefinition = new ClassSet(componentName, Lists.newArrayList(componentName + pathSeparator + "*"));
        apiDefinition.setPathSeparator(pathSeparator);
        result.getApi().put(componentName, apiDefinition);

        return result;
    }

    /**
     * Get the component name from the given node name. Uses {@link #pathSplitterRegExp} to split the name into parts,
     * which are interpreted as a hierarchical name structure.
     * <p>
     * E.g., for Java class names, it returns the package name.
     *
     * @param name      the node name
     * @param maxLength the maximum depth of the returned package name. Full package name if this is <tt>0</tt>.
     * @return the package name
     */
    /* package */
    String getComponentName(String name, int maxLength) {
        checkNotNull(name, "name");

        String[] parts = name.split(pathSplitterRegExp, maxLength == 0 ? 0 : (maxLength + 1));
        if (parts.length == 1) {
            return null;
        }

        int endIndex = maxLength == 0 ? (parts.length - 1) : Math.min(maxLength, parts.length - 1);
        return StringUtils.join(parts, pathSeparator, 0, endIndex);
    }
}
