package de.qaware.qav.input.typescript

import de.qaware.qav.graph.api.Constants
import de.qaware.qav.graph.api.Dependency
import de.qaware.qav.graph.api.DependencyGraph
import de.qaware.qav.graph.api.DependencyType
import de.qaware.qav.graph.api.Node as QavNode
import groovy.util.logging.Slf4j

/**
 * Reads an input file from the Typescript dependency analyzer, and writes those classes, methods, and relations
 * into the given {@link DependencyGraph}.
 *
 * The Typescript dependency analyzer creates an XML export file with nodes and edges.
 *
 * @author QAware GmbH
 */
@Slf4j
class TypescriptInputReader {

    public static final String ROOT_NODE_NAME = "Typescript_ROOT"
    public static final String PARENT_PROPERTY = "typescript" + Constants.PARENT_SUFFIX
    final DependencyGraph dependencyGraph
    Map<Integer, QavNode> nodeMap = [:]
    String currentFilename

    /**
     * Constructor.
     *
     * @param dependencyGraph the {@link DependencyGraph} where to add the nodes and edges
     */
    TypescriptInputReader(DependencyGraph dependencyGraph) {
        this.dependencyGraph = dependencyGraph
    }

    /**
     * Read the given file.
     *
     * @param filename the file to read
     * @throws IllegalArgumentException if the file does not exist
     */
    void read(String filename) {
        File inputFile = new File(filename)
        if (!inputFile.exists()) {
            throw new IllegalArgumentException("File ${inputFile.absolutePath} does not exist")
        }

        Node input = new XmlParser(false, false).parse(inputFile)

        readNodes(input)
        readDependencies(input)
    }

    private void readNodes(Node input) {
        QavNode rootNode = dependencyGraph.getOrCreateNodeByName(ROOT_NODE_NAME)

        List<Node> e = input.entities.entity
        e.each {entity ->
            addEntity(entity, rootNode)
        }
    }

    private void addEntity(Node entity, QavNode parentNode) {
        String name = entity['@name']
        String nodeName = (parentNode.name == ROOT_NODE_NAME) ? name : "${parentNode.getName()}#${name}"

        log.debug("Entity: ${nodeName}")
        QavNode qavNode = dependencyGraph.getOrCreateNodeByName(nodeName)
        dependencyGraph.addDependency(parentNode, qavNode, DependencyType.CONTAINS)

        qavNode.setProperty("scope", "typescript")
        String type = entity['@type'] as String
        qavNode.setProperty("typescript.type", type)
        Integer id = Integer.parseInt(entity['@id'] as String)
        qavNode.setProperty("typescript.id", id)
        nodeMap[id] = qavNode

        // Prepare properties to make dependency mapping easier:
        // For each element (class, property, method, etc): note in which File it belongs.
        if (type == "File") {
            currentFilename = name
        } else {
            qavNode.setProperty(PARENT_PROPERTY, currentFilename)
        }

        entity.entity.each {
            addEntity(it, qavNode)
        }
    }

    private void readDependencies(Node input) {
        input.dependencies.dependency.each {
            addDependency(it)
        }
    }

    private void addDependency(Node dependencyNode) {
        Integer fromId = Integer.parseInt(dependencyNode['@from'] as String)
        QavNode fromNode = nodeMap[fromId]
        Integer toId = Integer.parseInt(dependencyNode['@to'] as String)
        QavNode toNode = nodeMap[toId]
        String type = dependencyNode['@type']

        DependencyType dependencyType = DEPENDENCY_TYPE_MAP.getOrDefault(type, DependencyType.REFERENCE)
        Dependency dependency = dependencyGraph.addDependency(fromNode, toNode, dependencyType)
        dependency.setProperty("typescript.type", type)
    }

    public static final Map<String, DependencyType> DEPENDENCY_TYPE_MAP = [
            "Import"          : DependencyType.REFERENCE,
            "Extends"         : DependencyType.INHERIT,
            "Implements"      : DependencyType.INHERIT,
            "GenericParameter": DependencyType.REFERENCE,
            "Creates"         : DependencyType.CREATE,
            "Parameter"       : DependencyType.REFERENCE,
            "Returns"         : DependencyType.REFERENCE,
            "Calls"           : DependencyType.READ_WRITE,
            "Argument"        : DependencyType.REFERENCE,
            "Member"          : DependencyType.REFERENCE,
            "Variable"        : DependencyType.REFERENCE,
            "ReadWrite"       : DependencyType.READ_WRITE,
            "DependsOn"       : DependencyType.REFERENCE
    ]
}
