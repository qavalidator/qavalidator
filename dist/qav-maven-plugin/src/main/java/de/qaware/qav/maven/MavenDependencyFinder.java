package de.qaware.qav.maven;


import de.qaware.qav.graph.api.Constants;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.Node;
import de.qaware.qav.graph.factory.DependencyGraphFactory;
import de.qaware.qav.graph.io.GraphReaderWriter;
import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;

import java.util.Locale;

/**
 * Find and analyze the dependencies to other projects.
 *
 * Do so by reading the {@link MavenProject} (instead of reading the pom.xml file); this way, the actual dependencies
 * are analyzed, respecting chosen profiles and including inherited dependencies.
 */
public class MavenDependencyFinder {

    /**
     * Attribute key to set the used Maven version.
     */
    public static final String MAVEN_VERSION_KEY = "maven.version";

    /**
     * Attribute key to a boolean flag which says whether the version is a snapshot version.
     */
    public static final String MAVEN_SNAPSHOT_KEY = "maven.snapshot";

    private final DependencyGraph dependencyGraph = DependencyGraphFactory.createGraph();

    /**
     * Find all dependencies from this project and all of its children, and add them to the {@link #dependencyGraph}.
     *
     * @param mavenProject the {@link MavenProject} to analyze.
     */
    public void findDependencies(MavenProject mavenProject) {
        Node fromNode = getNode(mavenProject);

        if (mavenProject.getParent() != null) {
            Node parentNode = getNode(mavenProject.getParent());
            dependencyGraph.addDependency(fromNode, parentNode, DependencyType.INHERIT);
        }

        mavenProject.getCollectedProjects().stream()
                .map(this::getNode)
                .forEach(child -> dependencyGraph.addDependency(fromNode, child, DependencyType.CONTAINS));

        mavenProject.getDependencies()
                .forEach(dep -> addDependency(fromNode, dep));

        mavenProject.getCollectedProjects().forEach(this::findDependencies);
    }

    /**
     * Getter.
     *
     * @return the {@link DependencyGraph}
     */
    public DependencyGraph getDependencyGraph() {
        return dependencyGraph;
    }

    /**
     * Write the {@link #dependencyGraph} to the given file name.
     *
     * @param filename the file to write to.
     */
    public void writeDependencyGraph(String filename) {
        GraphReaderWriter.write(this.dependencyGraph, filename);
    }

    private void addDependency(Node from, Dependency dependency) {
        Node toNode = getNode(dependency);
        DependencyType dependencyType = getDependencyType(dependency);
        dependencyGraph.addDependency(from, toNode, dependencyType);
    }

    private Node getNode(MavenProject project) {
        String name = project.getGroupId() + ":" + project.getArtifactId();
        Node node = findOrCreateNode(name, project.getVersion());
        node.setProperty(Constants.SCOPE, "input");
        return node;
    }

    private Node getNode(Dependency dependency) {
        String name = dependency.getGroupId() + ":" + dependency.getArtifactId();
        return findOrCreateNode(name, dependency.getVersion());
    }

    private Node findOrCreateNode(String name, String version) {
        Node node = dependencyGraph.getOrCreateNodeByName(name);
        node.setProperty(Constants.TYPE, "maven");
        node.setProperty(MAVEN_VERSION_KEY, version);
        node.setProperty(MAVEN_SNAPSHOT_KEY, version.endsWith("-SNAPSHOT"));

        return node;
    }

    /**
     * Type-safe method to get the {@link DependencyType} from the given {@link Dependency}'s scope.
     *
     * @param dependency the {@link Dependency}
     * @return the {@link DependencyType} which matches the type given in the dependency scope, or {@link
     * DependencyType#REFERENCE} if the scope can't be mapped to a known type.
     */
    private DependencyType getDependencyType(Dependency dependency) {
        try {
            return DependencyType.valueOf(dependency.getScope().toUpperCase(Locale.US));
        } catch (IllegalArgumentException e) {
            return DependencyType.REFERENCE;
        }
    }
}
