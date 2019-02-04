package de.qaware.qav.visualization.impl;

import de.qaware.qav.architecture.dsl.model.Architecture;
import de.qaware.qav.architecture.dsl.model.Component;
import de.qaware.qav.graph.api.Constants;
import de.qaware.qav.graph.api.Dependency;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.Node;
import de.qaware.qav.util.FileNameUtil;
import de.qaware.qav.util.FileSystemUtil;
import de.qaware.qav.util.StringTemplateUtil;
import de.qaware.qav.visualization.model.Abbreviation;
import lombok.extern.slf4j.Slf4j;
import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Export the given {@link DependencyGraph} into a GraphML file.
 * <p>
 * The file is meant for yEd.
 * It does not contain layout information. To see the graph, use yEd and use the Auto-Layout feature.
 *
 * Transforms {@link DependencyType#CONTAINS} relations to clusters i.e. to nested nodes.
 */
@Slf4j
public class GraphMLExporter {

    // identifiers to find the StringTemplates in the .stg file
    // *_ST are the StringTemplates in the file.
    // *_ATT are attributes in a StringTemplate.

    public static final String GRAPHML_STG = "/visualization/graphml.stg";
    public static final String FILE_ST = "file";
    public static final String NODE_ST = "umlClass";
    public static final String EDGE_ST = "edge";
    public static final String EDGE_LABEL_ST = "edgeLabel";
    public static final String COMPONENT_ST = "component";

    public static final String ID_ATT = "id";
    public static final String NAME_ATT = "name";
    public static final String NODES_ATT = "nodes";
    public static final String EDGES_ATT = "edges";
    public static final String EDGE_LABEL_ATT = "edgeLabel";

    private final DependencyGraph dependencyGraph;
    private final String fileNameBase;
    private final Architecture architecture;
    private final List<Abbreviation> abbreviations = new ArrayList<>();

    private final StringTemplateGroup templates;
    private final StringTemplate outGraphST;
    private final Set<String> exportedNodes = new HashSet<>();

    // ----- configuration -----

    /**
     * Configure the output: print labels or not?
     */
    private final boolean createEdgeLabels;

    // ----- describe the export run: how many nodes and edges were created?
    private int noNodes = 0;
    private int noEdges = 0;

    /**
     * Constructor.
     *
     * @param dependencyGraph   the {@link DependencyGraph} to export
     * @param fileNameBase      the file name base, used to set up the .dot and .png file name
     * @param architecture      the {@link Architecture} to use for clusters (nested nodes)
     * @param abbreviationsList the list of {@link Abbreviation}s, to create labels
     * @param createEdgeLabels  true to print edge labels, false to omit them
     */
    public GraphMLExporter(DependencyGraph dependencyGraph, String fileNameBase, Architecture architecture,
                           List<Abbreviation> abbreviationsList,
                           boolean createEdgeLabels) {
        this.dependencyGraph = dependencyGraph;
        this.fileNameBase = fileNameBase;
        this.architecture = architecture;
        this.abbreviations.addAll(abbreviationsList);

        this.createEdgeLabels = createEdgeLabels;

        templates = StringTemplateUtil.loadTemplateGroupDollarSign(GRAPHML_STG);
        outGraphST = templates.getInstanceOf(FILE_ST);

        outGraphST.setAttribute("gname", "QAvalidator");
    }

    /**
     * Do the export.
     */
    public void exportGraph() {
        writeArchitecture();
        writeNodes();
        writeEdges();
        finish();
    }

    // === Architecture ===

    private void writeArchitecture() {
        outGraphST.setAttribute(NODES_ATT, createComponentST(architecture));
    }

    /**
     * Traverses down the architecture tree.
     * <p>
     * Creates a {@link StringTemplate} for the given component - but only if the {@link DependencyGraph} contains
     * a matching node.
     * This way, no architecture node is exported if it is filtered from the graph.
     * <p>
     * We rely on the fact that {@link StringTemplate#setAttribute(String, Object)} ignores null values.
     *
     * @param component the {@link Component}
     * @return the {@link StringTemplate}
     */
    private StringTemplate createComponentST(Component component) {
        if (!dependencyGraph.hasNode(component.getName())) {
            return null;
        }

        // create this node
        noNodes++;
        exportedNodes.add(component.getName());

        // here, we check if the current node is a leaf. If yes, we'll use the same yEd element as for leaf nodes.
        // Note, however, that a component which has children which are ignored in this graph (e.g. a package that
        // has sub-packages which are not part of this export), then this simple check will not work.
        // This is done on purpose, as the exported graph suggests that there is more, it's just not shown here.
        // If this is not the desired output, then this check here must be smarter.
        List<Component> nestedComponents = component.getChildren();
        List<Node> leafs = getChildren(component.getName());
        boolean isLeaf = nestedComponents.isEmpty() && leafs.isEmpty();

        StringTemplate componentST = templates.getInstanceOf(isLeaf ? NODE_ST : COMPONENT_ST);
        componentST.setAttribute(ID_ATT, GraphExportStyles.getId(component.getName()));
        componentST.setAttribute(NAME_ATT, GraphExportStyles.getLabel(abbreviations, component.getName()));

        // go down the architecture tree
        nestedComponents.forEach(child -> componentST.setAttribute("children", createComponentST(child)));

        // add leafs, i.e. those nodes that are not part of the architecture tree
        leafs.forEach(node -> componentST.setAttribute("children", createNodeST(node)));

        return componentST;
    }

    private List<Node> getChildren(String componentName) {
        Node node = dependencyGraph.getNode(componentName);
        Set<Dependency> outgoingEdges = dependencyGraph.getOutgoingEdges(node, DependencyType.CONTAINS);
        return outgoingEdges.stream()
                .map(Dependency::getTarget)
                .filter(n -> !"architecture".equals(n.getProperty(Constants.TYPE)))
                .collect(Collectors.toList());
    }

    // === Nodes ===

    /**
     * export all remaining nodes, i.e. those that we didn't cover while exporting the architecture.
     */
    private void writeNodes() {
        dependencyGraph.getAllNodes().stream()
                .filter(node -> !exportedNodes.contains(node.getName()))
                .forEach(node -> outGraphST.setAttribute(NODES_ATT, createNodeST(node)));
    }

    private StringTemplate createNodeST(Node node) {
        noNodes++;
        StringTemplate nodeST = templates.getInstanceOf(NODE_ST);
        nodeST.setAttribute(ID_ATT, GraphExportStyles.getId(node.getName()));
        nodeST.setAttribute(NAME_ATT, GraphExportStyles.getLabel(abbreviations, node.getName()));
        return nodeST;
    }

    // === Edges ===

    /**
     * writes all edges except CONTAINS, as the contains-relation is visualized by nested nodes.
     */
    private void writeEdges() {
        dependencyGraph.getAllEdges().stream()
                .filter(dependency -> dependency.getDependencyType() != DependencyType.CONTAINS)
                .forEach(dependency -> outGraphST.setAttribute(EDGES_ATT, createEdgeST(dependency)));
    }

    private StringTemplate createEdgeST(Dependency dependency) {
        noEdges++;
        StringTemplate edgeST = templates.getInstanceOf(EDGE_ST);
        edgeST.setAttribute("from", GraphExportStyles.getId(dependency.getSource().getName()));
        edgeST.setAttribute("to", GraphExportStyles.getId(dependency.getTarget().getName()));
        EdgeStyle style = GraphExportStyles.getEdgeStyle(dependency);
        edgeST.setAttribute("color", style.getColor());
        edgeST.setAttribute("style", style.getLineStyle());
        edgeST.setAttribute("width", style.getWidth());

        if (createEdgeLabels) {
            if (dependency.hasProperty(Constants.BASE_REL_COUNT)) {
                edgeST.setAttribute(EDGE_LABEL_ATT, createEdgeLabelST(getCounter(dependency, Constants.BASE_REL_COUNT), Position.MIDDLE, style.getColor()));
            }
            if (dependency.hasProperty(Constants.BASE_REL_COUNT_TARGETS)) {
                edgeST.setAttribute(EDGE_LABEL_ATT, createEdgeLabelST(getCounter(dependency, Constants.BASE_REL_COUNT_TARGETS), Position.HEAD, style.getColor()));
            }
            if (dependency.hasProperty(Constants.BASE_REL_COUNT_SOURCES)) {
                edgeST.setAttribute(EDGE_LABEL_ATT, createEdgeLabelST(getCounter(dependency, Constants.BASE_REL_COUNT_SOURCES), Position.TAIL, style.getColor()));
            }
        }

        return edgeST;
    }

    /**
     * Gets the counter label for the arrow head, tail, or middle.
     * <p>
     * Usually, this is the counter, i.e. an Integer. However, for the Legend creation, we write the Dependency type's
     * name, so it's a String. Therefore, this method returns Object.
     *
     * @param dependency the dependency
     * @param key        the property key to read
     * @return the value of the property, or "0" as default
     */
    private Object getCounter(Dependency dependency, String key) {
        Object counter = dependency.getProperty(key);
        if (counter == null) {
            counter = 0;
        }
        return counter;
    }

    private enum Position {

        HEAD("1.0"),
        TAIL("0.0"),
        MIDDLE("0.5");

        private String posValue;

        Position(String posValue) {
            this.posValue = posValue;
        }

        public String getPosValue() {
            return posValue;
        }
    }

    private StringTemplate createEdgeLabelST(Object counter, Position position, String color) {
        StringTemplate edgeLabelST = templates.getInstanceOf(EDGE_LABEL_ST);
        edgeLabelST.setAttribute("name", counter);
        edgeLabelST.setAttribute("position", position.getPosValue());
        edgeLabelST.setAttribute("color", color);
        return edgeLabelST;
    }

    // === finish ===

    private void finish() {
        String filename = FileNameUtil.getCanonicalPath(fileNameBase + ".graphml");
        if (noNodes == 0) {
            LOGGER.info("Graph is empty. Not writing graph '{}'", filename);
            return;
        }

        LOGGER.info("Writing graph with {} nodes and {} edges to file '{}'.", noNodes, noEdges, filename);
        FileSystemUtil.writeStringToFile(outGraphST.toString(), filename);
    }

}
