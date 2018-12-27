package de.qaware.qav.app.server.controller;

import de.qaware.qav.app.server.exceptions.NotFoundException;
import de.qaware.qav.app.server.mapper.DependencyMapper;
import de.qaware.qav.app.server.mapper.NodeMapper;
import de.qaware.qav.app.server.model.DependencyDTO;
import de.qaware.qav.app.server.model.GraphInfoDTO;
import de.qaware.qav.app.server.model.NodeDTO;
import de.qaware.qav.graph.api.AbstractGraphElement;
import de.qaware.qav.graph.api.Dependency;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.Node;
import de.qaware.qav.graph.index.DependencyGraphIndex;
import de.qaware.qav.graph.io.GraphReaderWriter;
import de.qaware.qav.util.FileNameUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * REST controller to access the Dependency Graph.
 * <p>
 * This controller reads the JSON file and keeps it in memory. It creates a temporary Lucene index to offer a nice and
 * flexible search interface.
 *
 * @author QAware GmbH
 */
@RestController
@Slf4j
public class GraphController {

    private String filename;

    private DependencyGraph dependencyGraph;
    private DependencyGraphIndex index;

    /**
     * Setter for the file name.
     *
     * @param filename graph file to read.
     */
    @Value("${de.qaware.qav.graph.filename}")
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * Read the graph file.
     * <p>
     * Check that the file exists, and create the search index.
     */
    @PostConstruct
    public void init() {
        LOGGER.info("Reading Graph from file: {}", FileNameUtil.getCanonicalPath(filename));
        File graphFile = new File(filename);
        assertFileExists(graphFile);
        dependencyGraph = GraphReaderWriter.read(filename);
        LOGGER.info("Graph with: {} nodes and {} edges",
                dependencyGraph.getAllNodes().size(), dependencyGraph.getAllEdges().size());

        index = new DependencyGraphIndex(dependencyGraph);
    }

    private void assertFileExists(File file) {
        if (!file.exists()) {
            throw new IllegalArgumentException("File not found: " + file.getAbsolutePath());
        }
    }

    /**
     * Sample, so far.
     *
     * @return some basic info about the graph
     */
    @GetMapping(value = "/info")
    public GraphInfoDTO getInfo() {
        String info = MessageFormat.format("Graph with: {0} nodes and {1} edges. Filename: {2}",
                dependencyGraph.getAllNodes().size(), dependencyGraph.getAllEdges().size(),
                FileNameUtil.getCanonicalPath(filename));

        LOGGER.info("Request to /info: {}", info);
        GraphInfoDTO result = new GraphInfoDTO();
        result.setInfo(info);
        return result;
    }

    /**
     * Find the node with the given name.
     *
     * @param name the name of the node
     * @return the node
     * @throws NotFoundException if the node was not found
     */
    @GetMapping(value = "/node")
    public NodeDTO getNode(@RequestParam(value = "name") String name) {
        LOGGER.info("Node: {}", name);

        Node result = findNode(name);
        return NodeMapper.toDTO(result, dependencyGraph);
    }

    private Node findNode(String name) {
        Node result = dependencyGraph.getNode(name);
        if (result == null) {
            String message = MessageFormat.format("Node {0} not found", name);
            LOGGER.error(message);
            throw new NotFoundException(message);
        }
        return result;
    }

    /**
     * Find the edge from the given source node to the given target node.
     *
     * @param from source node name
     * @param to   target node name
     * @return the {@link DependencyDTO} if the edge was found.
     * @throws NotFoundException if one of the nodes or the edge was not found.
     */
    @GetMapping(value = "/edge")
    public DependencyDTO getEdge(@RequestParam("from") String from,
                                 @RequestParam("to") String to) {
        LOGGER.info("Edge: from {} to {}", from, to);

        Node fromNode = findNode(from);
        Node toNode = findNode(to);

        Dependency edge = dependencyGraph.getEdge(fromNode, toNode);
        if (edge == null) {
            String message = MessageFormat.format("No edge from {0} to {1}", from, to);
            LOGGER.error(message);
            throw new NotFoundException(message);
        }

        return DependencyMapper.toDTO(edge);
    }

    /**
     * Find all nodes, paged.
     *
     * @param query    query string; optional, defaults to "find all". If given, uses the Lucene query syntax to find
     *                 names
     * @param pageable page information; optional, defaults to page 0 and size 20
     * @return the result page
     */
    @GetMapping(value = "/nodes")
    public Page<Node> getNodes(@RequestParam(value = "q", required = false) String query,
                               Pageable pageable) {
        LOGGER.info("Get all nodes. Query: {}, Paging: {}", query, pageable);

        List<Node> resultList;
        if (query != null) {
            String normalizedQuery = normalizeQuery(query);
            resultList = new ArrayList<>(index.findNodes(normalizedQuery));
        } else {
            resultList = new ArrayList<>(dependencyGraph.getAllNodes());
        }

        resultList.sort(Comparator.comparing(AbstractGraphElement::getName));

        int startIndex = (int) pageable.getOffset();
        if (startIndex > resultList.size()) {
            startIndex = resultList.size();
        }
        int stopIndex = startIndex + pageable.getPageSize();
        if (stopIndex > resultList.size()) {
            stopIndex = resultList.size();
        }
        List<Node> subList = resultList.subList(startIndex, stopIndex);

        return new PageImpl<>(subList, pageable, resultList.size());
    }

    /**
     * If the query does not appear to be a Lucene query, assume the user wants to search by name. Prepends "name:" in
     * that case.
     *
     * @param query the query string
     * @return the query, with a "name:" prefix if there is no ":" in the original query.
     */
    private String normalizeQuery(String query) {
        return query.indexOf(':') >= 0
                ? query
                : ("name:" + query.trim());
    }
}
