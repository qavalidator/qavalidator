package de.qaware.qav.input.maven;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import de.qaware.qav.graph.api.Constants;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.Node;
import de.qaware.qav.input.maven.model.MavenDependency;
import de.qaware.qav.input.maven.model.MavenProject;
import de.qaware.qav.util.FileSystemUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

/**
 * Reads Maven POMs with their structure and their dependencies.
 *
 * The "module" relationship is mapped to {@link DependencyType#CONTAINS}.
 * The "parent" relationship is mapped to {@link DependencyType#INHERIT}.
 */
@Slf4j
public class MavenInputReader {

    private final DependencyGraph dependencyGraph;
    private final XmlMapper xmlMapper = new XmlMapper();

    /**
     * Constructor.
     *
     * @param dependencyGraph the {@link DependencyGraph}
     */
    public MavenInputReader(DependencyGraph dependencyGraph) {
        this.dependencyGraph = dependencyGraph;
    }

    public Node readPom(String dirname) {
        LOGGER.debug("Read pom file in directory: {}", dirname);
        FileSystemUtil.assertFileOrResourceExists(dirname);
        MavenProject bean = readFile(dirname);
        Node node = mapInput(bean);
        node.setProperty(Constants.SCOPE, "input");

        bean.getModules().forEach(module -> {
            Node child = readPom(dirname + "/" + module);
            dependencyGraph.addDependency(node, child, DependencyType.CONTAINS);
        }); // recursively read the child pom.xml

        return node;
    }

    private MavenProject readFile(String dirname) {
        File file = new File(dirname, "pom.xml");
        try {
            return xmlMapper.readValue(file, MavenProject.class);
        } catch (IOException e) {
            LOGGER.error("Can't read file {}", file.getAbsolutePath(), e);
            throw new IllegalArgumentException(e);
        }
    }

    private Node mapInput(MavenProject mavenProject) {
        LOGGER.info("Read: {}", mavenProject);

        if (mavenProject.getGroupId() == null || "${project.groupId}".equals(mavenProject.getGroupId())) {
            // in this case, parent MUST be set.
            mavenProject.setGroupId(mavenProject.getParent().getGroupId());
        }
        Node node = getOrCreateNode(mavenProject.getGroupId(), mavenProject.getArtifactId());

        if (mavenProject.getParent() != null) {
            Node parent = getOrCreateNode(mavenProject.getParent().getGroupId(), mavenProject.getParent().getArtifactId());
            dependencyGraph.addDependency(node, parent, DependencyType.INHERIT);
        }

        mavenProject.getDependencies().forEach(dep -> addDependency(node, dep, mavenProject.getGroupId()));

        return node;
    }

    private void addDependency(Node from, MavenDependency mavenDependency, String fallbackGroupId) {
        String groupId = mavenDependency.getGroupId();
        if ("${project.groupId}".equals(groupId)) {
            groupId = fallbackGroupId;
        }
        String artifactId = mavenDependency.getArtifactId();
        Node to = getOrCreateNode(groupId, artifactId);
        DependencyType type = mavenDependency.getScope() != null ?
                DependencyType.valueOf(mavenDependency.getScope().toUpperCase(Locale.US))
                : DependencyType.COMPILE;

        dependencyGraph.addDependency(from, to, type);
    }

    /**
     * Creates a {@link Node} for the given groupId and artifactId.
     *
     * @param groupId    the groupId
     * @param artifactId the artifactId
     */
    private Node getOrCreateNode(String groupId, String artifactId) {
        String nodeName = groupId + ":" + artifactId;
        Node node = dependencyGraph.getOrCreateNodeByName(nodeName);
        node.setProperty(Constants.TYPE, "maven");
        return node;
    }
}
