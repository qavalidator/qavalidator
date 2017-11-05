package de.qaware.qav.input.maven

import de.qaware.qav.graph.api.Constants
import de.qaware.qav.graph.api.DependencyGraph
import de.qaware.qav.graph.api.DependencyType
import de.qaware.qav.graph.api.Node
import groovy.util.logging.Slf4j

/**
 * Reads Maven POMs with their structure and their dependencies.
 *
 * The "module" relationship is mapped to {@link DependencyType#CONTAINS}.
 * The "parent" relationship is mapped to {@link DependencyType#INHERIT}.
 *
 * @author QAware GmbH
 */
@Slf4j
class MavenInputReader {

    private DependencyGraph dependencyGraph

    /**
     * Creates a new Reader for pom.xml files.
     *
     * @param dependencyGraph the dependency graph to work on
     */
    MavenInputReader(DependencyGraph dependencyGraph) {
        this.dependencyGraph = dependencyGraph
    }

    /**
     * Finds all pom files in root dir, reads them, and writes output in the dependencyGraph.
     *
     * @param rootDirName the root dir of the project to search in
     */
    Node readPom(String rootDirName) {
        File rootDir = new File(rootDirName)
        if (!rootDir.exists() || !rootDir.isDirectory()) {
            throw new IllegalArgumentException("Input directory ${rootDir.absolutePath} does not exist or is no directory.")
        }

        return readRecursively(new File(rootDir, "pom.xml"))
    }

    /**
     * Reads the dependencies in the current pom file and adds them to the dependency graph.
     *
     * @param pomFile the pom file
     */
    private Node readRecursively(File pomFile) {
        // Reads the pom
        groovy.util.Node pom
        try {
            pom = new XmlParser(false, false).parse(pomFile)
        } catch (IOException e) {
            log.error("Could not find pom file: ${pomFile.getAbsolutePath()}: ${e.getMessage()}")
            return null
        }

        Node node = createPomNode(pom)

        // Read module files
        pom.modules.module.each {m ->
            String moduleName = m.text()
            String moduleFileName = "${pomFile.getParent()}/${moduleName}/pom.xml"
            def childNode = readRecursively(new File(moduleFileName))
            dependencyGraph.addDependency(node, childNode, DependencyType.CONTAINS)
        }

        addDependencies(node, pom)

        return node
    }

    /**
     * Create a node for a POM module which is part of the POM tree, i.e. part of the input scope.
     *
     * @param pom the POM XML node
     * @return the new node
     */
    private Node createPomNode(groovy.util.Node pom) {
        String groupId = pom.groupId.text()
        String artifactId = pom.artifactId.text()
        if (groupId == "" || groupId == "\${project.groupId}") {
            groupId = pom.parent.groupId.text()
        }

        Node node = createNode(groupId, artifactId)
        node.setProperty(Constants.SCOPE, "input")

        return node
    }

    /**
     * Create nodes for referenced POM modules, and the dependencies to these nodes.
     * The modules may be part of the POM tree or may be external, so they will not be marked as part of the input scope.
     * (If they are internal, {@link #createPomNode(groovy.util.Node)} marks them as part of the input scope.)
     *
     * @param node the node which represents the POM module, i.e. the source node for the dependencies
     * @param pom the POM XML node
     */
    private void addDependencies(Node node, groovy.util.Node pom) {
        // Create parent relationship, if pom defines a parent explicitly
        String parentGroupId
        if (pom.parent) {
            parentGroupId = pom.parent.groupId.text()
            String partentArtifactId = pom.parent.artifactId.text()
            Node targetNode = createNode(parentGroupId, partentArtifactId)
            dependencyGraph.addDependency(node, targetNode, DependencyType.INHERIT)
        }

        pom.dependencies.dependency.each {d ->
            String depGroupId = d.groupId.text()
            String depArtifactId = d.artifactId.text()

            if (depGroupId == "\${project.groupId}") {
                depGroupId = parentGroupId
            }

            DependencyType type = d.scope ?
                    DependencyType.valueOf(d.scope.text().toUpperCase())
                    : DependencyType.COMPILE

            Node targetNode = createNode(depGroupId, depArtifactId)
            dependencyGraph.addDependency(node, targetNode, type)
        }
    }

    /**
     * Creates a {@link Node} with the given name and the properties we need.
     *
     * @param groupId the groupId of the node
     * @param artifactId the artifactId of the node
     * @return the new node
     */
    private Node createNode(String groupId, String artifactId) {
        String nodeName = groupId + ":" + artifactId
        Node node = dependencyGraph.getOrCreateNodeByName(nodeName)
        node.setProperty(Constants.TYPE, "maven")
        return node
    }
}
