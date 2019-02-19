package de.qaware.qav.gradle;

import de.qaware.qav.graph.api.Constants;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.Node;
import de.qaware.qav.graph.factory.DependencyGraphFactory;
import de.qaware.qav.graph.io.GraphReaderWriter;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Dependency;

import java.util.HashMap;
import java.util.Map;

/**
 * Find and analyze the dependencies to other projects.
 *
 * Do so by reading the {@link Project} (instead of reading the build.gradle files); this way, the actual dependencies
 * are analyzed, respecting chosen profiles and including inherited dependencies.
 */
public class GradleDependencyFinder {

    /**
     * Attribute key to set the used Gradle/Maven version.
     */
    public static final String GRADLE_VERSION_KEY = "maven.version";

    /**
     * Attribute key to a boolean flag which says whether the version is a snapshot version.
     */
    public static final String GRADLE_SNAPSHOT_KEY = "maven.snapshot";

    private static Map<String, DependencyType> dependencyTypeMap = new HashMap<>();

    static {
        dependencyTypeMap.put("api", DependencyType.COMPILE);
        dependencyTypeMap.put("implementation", DependencyType.COMPILE);
        dependencyTypeMap.put("compile", DependencyType.COMPILE);
        dependencyTypeMap.put("testCompile", DependencyType.TEST);
    }

    private final DependencyGraph dependencyGraph = DependencyGraphFactory.createGraph();

    /**
     * Find all dependencies from this project and all of its children, and add them to the {@link #dependencyGraph}.
     *
     * @param gradleProject the {@link Project} to analyze.
     */
    public void findDependencies(Project gradleProject) {
        Node fromNode = getNode(gradleProject);

        if (gradleProject.getParent() != null) {
            Node parentNode = getNode(gradleProject.getParent());
            dependencyGraph.addDependency(fromNode, parentNode, DependencyType.INHERIT);
        }

        gradleProject.getChildProjects().values().stream()
                .map(this::getNode)
                .forEach(child -> dependencyGraph.addDependency(fromNode, child, DependencyType.CONTAINS));

        gradleProject.getConfigurations().forEach(
                configuration -> configuration.getDependencies().forEach(dep -> addDependency(fromNode, dep, configuration.getName()))
        );

        gradleProject.getChildProjects().values().forEach(this::findDependencies);
    }

    /**
     * Getter.
     *
     * @return the{@link DependencyGraph}
     */
    public DependencyGraph getDependencyGraph() {
        return dependencyGraph;
    }

    /**
     * Write the {@link #dependencyGraph} to the given file name.
     *
     * @param filename the file to write to.
     */
    void writeDependencyGraph(String filename) {
        GraphReaderWriter.write(this.dependencyGraph, filename);
    }

    private void addDependency(Node from, Dependency dependency, String configurationName) {
        Node toNode = getNode(dependency);
        DependencyType dependencyType = getDependencyType(configurationName);
        dependencyGraph.addDependency(from, toNode, dependencyType);
    }

    private Node getNode(Project project) {
        String name = project.getGroup().toString() + ":" + project.getName();
        Node node = findOrCreateNode(name, project.getVersion().toString());
        node.setProperty(Constants.SCOPE, "input");
        return node;
    }

    private Node getNode(Dependency dependency) {
        String name = dependency.getGroup() + ":" + dependency.getName();
        return findOrCreateNode(name, dependency.getVersion());
    }

    private Node findOrCreateNode(String name, String version) {
        Node node = dependencyGraph.getOrCreateNodeByName(name);
        node.setProperty(Constants.TYPE, "maven");
        node.setProperty(GRADLE_VERSION_KEY, version);
        if (version != null) {
            node.setProperty(GRADLE_SNAPSHOT_KEY, version.endsWith("-SNAPSHOT"));
        }

        return node;
    }

    /**
     * Type-safe method to get the {@link DependencyType} from the given {@link Dependency}'s scope.
     *
     * @param configurationName the name of the configuration in which the dependency was declared
     * @return the{@link DependencyType} which matches the type given in the dependency scope, or {@link
     * DependencyType#REFERENCE} if the scope can't be mapped to a known type.
     */
    private DependencyType getDependencyType(String configurationName) {
        if (dependencyTypeMap.containsKey(configurationName)) {
            return dependencyTypeMap.get(configurationName);
        }

        try {
            return DependencyType.valueOf(configurationName);
        } catch (IllegalArgumentException e) {
            System.out.println("configurationName: " + configurationName);
            return DependencyType.REFERENCE;
        }
    }
}
