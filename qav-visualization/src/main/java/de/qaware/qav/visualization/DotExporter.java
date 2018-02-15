package de.qaware.qav.visualization;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import de.qaware.qav.architecture.dsl.model.Architecture;
import de.qaware.qav.graph.api.Constants;
import de.qaware.qav.graph.api.Dependency;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.Node;
import de.qaware.qav.graph.filter.NodeHasOutgoingDependencyTypeFilter;
import de.qaware.qav.util.FileNameUtil;
import de.qaware.qav.util.FileSystemUtil;
import de.qaware.qav.util.ProcessUtil;
import de.qaware.qav.util.StringTemplateUtil;
import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Export the given {@link DependencyGraph} into a dot file.
 *
 * @author QAware GmbH
 */
public class DotExporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(DotExporter.class);

    /**
     * DOT has problems creating large graphs. We only call dot if the number of nodes allows the creation of a PNG.
     */
    public static final int MAX_NODES_FOR_DOT_CREATION = 65;

    public static final String DOT_STG = "/visualization/DOT.stg";
    public static final String FILE_ST = "file";
    public static final String NODE_ST = "node";
    public static final String EDGE_ST = "edge";
    public static final String CLUSTER_ST = "cluster";

    public static final String ID_ATT = "id";
    public static final String NAME_ATT = "name";
    public static final String URL_ATT = "url";
    public static final String LABEL_ATT = "label";
    public static final String HEAD_LABEL_ATT = "headlabel";
    public static final String TAIL_LABEL_ATT = "taillabel";
    public static final String STYLE_ATT = "style";
    public static final String NODES_ATT = "nodes";
    public static final String EDGES_ATT = "edges";
    public static final String CLUSTERS_ATT = "clusters";
    public static final String FROM_ATT = "from";
    public static final String TO_ATT = "to";

    private final DependencyGraph dependencyGraph;
    private final String fileNameBase;
    private final Architecture architecture;
    private final Map<String, StringTemplate> clusterMap = Maps.newHashMap();
    private final List<Abbreviation> abbreviations = new ArrayList<>();

    private final StringTemplateGroup templates;
    private final StringTemplate outGraphST;

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
    public DotExporter(DependencyGraph dependencyGraph, String fileNameBase, Architecture architecture,
                       List<Abbreviation> abbreviationsList,
                       boolean createEdgeLabels) {
        this.dependencyGraph = dependencyGraph;
        this.fileNameBase = fileNameBase;
        this.architecture = architecture;
        this.abbreviations.addAll(abbreviationsList);

        this.createEdgeLabels = createEdgeLabels;

        templates = StringTemplateUtil.loadTemplateGroupAngleBracket(DOT_STG);
        outGraphST = templates.getInstanceOf(FILE_ST);
    }

    /**
     * Do the export.
     */
    public void exportGraph() {
        writeClusters();
        writeNodes();
        writeEdges();
        finish();
    }

    // === Clusters ===

    private void writeClusters() {
        // step 1: create StringTemplates for clusters.
        DependencyGraph architectureGraph = dependencyGraph.filter(new NodeHasOutgoingDependencyTypeFilter(dependencyGraph, DependencyType.CONTAINS));
        for (Node node : architectureGraph.getAllNodes()) {
            StringTemplate clusterST = createClusterST(node);
            clusterMap.put(node.getName(), clusterST);
        }

        // step 2: create the order between the cluster STs.
        for (Map.Entry<String, StringTemplate> entry : clusterMap.entrySet()) {
            getParentST(entry.getKey()).setAttribute(CLUSTERS_ATT, entry.getValue());
        }
    }

    private StringTemplate createClusterST(Node node) {
        noNodes++;
        StringTemplate clusterST = templates.getInstanceOf(CLUSTER_ST);
        clusterST.setAttribute(ID_ATT, GraphExportStyles.getId(node.getName()));
        clusterST.setAttribute(LABEL_ATT, GraphExportStyles.getLabel(abbreviations, node.getName()));
        clusterST.setAttribute(URL_ATT, DotExportStyles.getNodeUrl(node.getName()));

        // only draw API node if it is used explicitly:
        Set<Dependency> relevantEdges = Sets.newHashSet();
        relevantEdges.addAll(dependencyGraph.getIncomingEdges(node));
        relevantEdges.addAll(dependencyGraph.getOutgoingEdges(node));
        relevantEdges.removeAll(dependencyGraph.getIncomingEdges(node, DependencyType.CONTAINS));
        relevantEdges.removeAll(dependencyGraph.getOutgoingEdges(node, DependencyType.CONTAINS));
        clusterST.setAttribute("setAPINode", !relevantEdges.isEmpty());

        return clusterST;
    }

    // === Nodes ===

    private void writeNodes() {
        dependencyGraph.getAllNodes().stream()
                .filter(node -> !clusterMap.containsKey(node.getName()))
                .forEach(node -> {
                    StringTemplate nodeST = createNodeST(node);
                    getParentST(node.getName()).setAttribute(NODES_ATT, nodeST);
                });

        outGraphST.setAttribute("ranksep", DotExportStyles.getRankSep(noNodes));
    }

    private StringTemplate getParentST(String nodeName) {
        if (architecture == null) {
            return outGraphST;
        } else {
            String parentName = architecture.getParentComponentName(nodeName);
            StringTemplate parentST = clusterMap.get(parentName);
            return parentST != null ? parentST : outGraphST;
        }
    }

    private StringTemplate createNodeST(Node node) {
        noNodes++;
        StringTemplate nodeST = templates.getInstanceOf(NODE_ST);
        nodeST.setAttribute(NAME_ATT, GraphExportStyles.getId(node.getName()));
        nodeST.setAttribute(LABEL_ATT, GraphExportStyles.getLabel(abbreviations, node.getName()));
        nodeST.setAttribute(STYLE_ATT, DotExportStyles.getNodeStyle(node));
        nodeST.setAttribute(URL_ATT, DotExportStyles.getNodeUrl(node.getName()));
        return nodeST;
    }

    // === Edges ===

    /**
     * writes all edges except CONTAINS, as the contains-relation is visualized by nested clusters.
     */
    private void writeEdges() {
        dependencyGraph.getAllEdges().stream()
                .filter(dependency -> !dependency.getDependencyType().equals(DependencyType.CONTAINS))
                .forEach(dependency -> outGraphST.setAttribute(EDGES_ATT, createEdgeST(dependency)));
    }

    private StringTemplate createEdgeST(Dependency dependency) {
        noEdges++;
        StringTemplate edgeST = templates.getInstanceOf(EDGE_ST);
        edgeST.setAttribute(FROM_ATT, GraphExportStyles.getId(dependency.getSource().getName()));
        edgeST.setAttribute(TO_ATT, GraphExportStyles.getId(dependency.getTarget().getName()));
        edgeST.setAttribute(STYLE_ATT, DotExportStyles.getEdgeStyle(dependency));

        if (createEdgeLabels) {
            if (dependency.hasProperty(Constants.BASE_REL_COUNT)) {
                edgeST.setAttribute(LABEL_ATT, dependency.getProperty(Constants.BASE_REL_COUNT));
            }
            if (dependency.hasProperty(Constants.BASE_REL_COUNT_TARGETS)) {
                edgeST.setAttribute(HEAD_LABEL_ATT, dependency.getProperty(Constants.BASE_REL_COUNT_TARGETS));
            }
            if (dependency.hasProperty(Constants.BASE_REL_COUNT_SOURCES)) {
                edgeST.setAttribute(TAIL_LABEL_ATT, dependency.getProperty(Constants.BASE_REL_COUNT_SOURCES));
            }
        }

        return edgeST;
    }

    // === finish ===

    /**
     * finish: write dot file, and trigger calling the dot program.
     */
    private void finish() {
        String filename = FileNameUtil.getCanonicalPath(fileNameBase + ".dot");
        if (noNodes == 0) {
            LOGGER.info("Graph is empty. Not writing graph '{}'", filename);
            return;
        }

        LOGGER.info("Writing graph with {} nodes and {} edges to file '{}'.", noNodes, noEdges, filename);
        FileSystemUtil.writeStringToFile(outGraphST.toString(), filename);

        if (noNodes < MAX_NODES_FOR_DOT_CREATION) {
            postProcessResults();
        } else {
            LOGGER.info("Not calling DOT - graph probably too large: {} nodes", noNodes);
        }
    }

    /**
     * Call <tt>dot</tt> to produce .png and .svg files.
     */
    private void postProcessResults() {
        String directory = ".";
        String dotFilename = fileNameBase + ".dot";
        String pngFilename = fileNameBase + ".png";
        String svgFilename = fileNameBase + ".svg";

        List<String> cmd = Arrays.asList("dot", "-Tpng", "-o", pngFilename, dotFilename);
        ProcessUtil.execProcess(directory, cmd);
        cmd = Arrays.asList("dot", "-Tsvg", "-o", svgFilename, dotFilename);
        ProcessUtil.execProcess(directory, cmd);
    }

}
