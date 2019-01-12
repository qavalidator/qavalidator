package de.qaware.qav.input.typescript;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import de.qaware.qav.graph.api.Constants;
import de.qaware.qav.graph.api.Dependency;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.Node;
import de.qaware.qav.input.typescript.model.TypescriptDependency;
import de.qaware.qav.input.typescript.model.TypescriptEntity;
import de.qaware.qav.input.typescript.model.TypescriptProject;
import de.qaware.qav.util.FileNameUtil;
import de.qaware.qav.util.FileSystemUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Reads an input file from the Typescript dependency analyzer, and writes those classes, methods, and relations into
 * the given {@link DependencyGraph}.
 * <p>
 * The Typescript dependency analyzer (which is a piece of Typescript software that runs in the Typescript/JavaScript
 * build process) creates an XML export file with nodes and edges.
 */
@Slf4j
public class TypescriptInputReader {

    public static final String ROOT_NODE_NAME = "Typescript_ROOT";
    public static final String PARENT_PROPERTY = "typescript" + Constants.PARENT_SUFFIX;

    private final DependencyGraph dependencyGraph;
    private final XmlMapper xmlMapper = new XmlMapper();
    private final Map<String, Node> nodeMap = new HashMap<>();

    private String currentFilename;

    /**
     * Reads typescript analysis from the specified XML file and puts the nodes and edges into the given target graph.
     *
     * @param dependencyGraph the target graph
     */
    public TypescriptInputReader(DependencyGraph dependencyGraph) {
        this.dependencyGraph = dependencyGraph;
    }

    /**
     * Read the given XML file.
     *
     * @param filename the file to read
     */
    public void read(String filename) {
        FileSystemUtil.assertFileOrResourceExists(filename);
        TypescriptProject bean = readFile(filename);
        mapInput(bean);
    }

    private TypescriptProject readFile(String filename) {
        try {
            File file = new File(filename);
            return xmlMapper.readValue(file, TypescriptProject.class);
        } catch (IOException e) {
            LOGGER.error("Can't read file {}", FileNameUtil.getCanonicalPath(filename), e);
            throw new IllegalArgumentException(e);
        }
    }

    private void mapInput(TypescriptProject bean) {
        Node rootNode = dependencyGraph.getOrCreateNodeByName(ROOT_NODE_NAME);
        bean.getEntities().forEach(typescriptEntity -> inputNode(typescriptEntity, rootNode));
        bean.getDependencies().forEach(this::mapDependency);
    }

    private void inputNode(TypescriptEntity typescriptEntity, Node parentNode) {
        String nodeName = parentNode.getName().equals(ROOT_NODE_NAME) ? typescriptEntity.getName()
                : parentNode.getName() + "#" + typescriptEntity.getName();

        Node node = dependencyGraph.getOrCreateNodeByName(nodeName);
        dependencyGraph.addDependency(parentNode, node, DependencyType.CONTAINS);
        nodeMap.put(typescriptEntity.getId(), node);

        node.setProperty("scope", "typescript");
        node.setProperty("typescript.type", typescriptEntity.getType());
        Integer id = Integer.parseInt(typescriptEntity.getId());
        node.setProperty("typescript.id", id);

        // Prepare properties to make dependency mapping easier:
        // For each element (class, property, method, etc): note in which File it belongs.
        if (typescriptEntity.getType().equals("File")) {
            currentFilename = nodeName;
        } else {
            node.setProperty(PARENT_PROPERTY, currentFilename);
        }

        if (typescriptEntity.getEntities() != null) {
            typescriptEntity.getEntities().forEach(child -> inputNode(child, node));
        }
    }

    private void mapDependency(TypescriptDependency dependency) {
        Node from = nodeMap.get(dependency.getFrom());
        Node to = nodeMap.get(dependency.getTo());
        DependencyType type = DEPENDENCY_TYPE_MAP.getOrDefault(dependency.getType(), DependencyType.REFERENCE);

        Dependency dep = dependencyGraph.addDependency(from, to, type);
        dep.setProperty("typescript.type", dependency.getType());
    }

    /**
     * Map from Typescript analyzer's dependency types to QAvalidator's {@link DependencyType}
     */
    private static final Map<String, DependencyType> DEPENDENCY_TYPE_MAP = new HashMap<>();

    static {
        DEPENDENCY_TYPE_MAP.put("Import", DependencyType.REFERENCE);
        DEPENDENCY_TYPE_MAP.put("Extends", DependencyType.INHERIT);
        DEPENDENCY_TYPE_MAP.put("Implements", DependencyType.INHERIT);
        DEPENDENCY_TYPE_MAP.put("GenericParameter", DependencyType.REFERENCE);
        DEPENDENCY_TYPE_MAP.put("Creates", DependencyType.CREATE);
        DEPENDENCY_TYPE_MAP.put("Parameter", DependencyType.REFERENCE);
        DEPENDENCY_TYPE_MAP.put("Returns", DependencyType.REFERENCE);
        DEPENDENCY_TYPE_MAP.put("Calls", DependencyType.READ_WRITE);
        DEPENDENCY_TYPE_MAP.put("Argument", DependencyType.REFERENCE);
        DEPENDENCY_TYPE_MAP.put("Member", DependencyType.REFERENCE);
        DEPENDENCY_TYPE_MAP.put("Variable", DependencyType.REFERENCE);
        DEPENDENCY_TYPE_MAP.put("ReadWrite", DependencyType.READ_WRITE);
        DEPENDENCY_TYPE_MAP.put("DependsOn", DependencyType.REFERENCE);
    }
}
