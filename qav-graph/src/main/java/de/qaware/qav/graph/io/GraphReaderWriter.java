package de.qaware.qav.graph.io;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.util.FileNameUtil;
import de.qaware.qav.util.FileSystemUtil;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Writes a graph to a file, and reads a graph from the file.
 * <p>
 * The file format is a plain JSON file;
 * <p>
 * <ul>
 * <li>the nodes are sorted alphabetically according to their names;</li>
 * <li>the edges are sorted alphabetically according to their source node name and then to their target node name,</li>
 * <li>and the properties for the nodes are also sorted alphabetically according to their key.</li>
 * </ul>
 *
 * @author QAware GmbH
 */
public final class GraphReaderWriter {

    private static final Logger LOGGER = getLogger(GraphReaderWriter.class);
    private static final IOGraphMapper IO_GRAPH_MAPPER = new IOGraphMapper();

    /**
     * util class, no instances.
     */
    private GraphReaderWriter() {
    }

    /**
     * Writes the given graph to the given file.
     *
     * @param filename        name of the file to write to.
     * @param dependencyGraph the graph.
     */
    public static void write(DependencyGraph dependencyGraph, String filename) {
        LOGGER.info("Writing graph with {} nodes and {} edges to '{}'",
                dependencyGraph.getAllNodes().size(),
                dependencyGraph.getAllEdges().size(),
                FileNameUtil.getCanonicalPath(filename));

        IOGraph ioGraph = IO_GRAPH_MAPPER.createIOGraph(dependencyGraph);

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        String jsonString;
        try {
            jsonString = mapper.writeValueAsString(ioGraph);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Error transforming to JSON: ", e);
        }

        FileSystemUtil.writeStringToFile(jsonString, filename);
    }

    /**
     * Reads the graph from the given file.
     *
     * @param filename name of the file to read.
     * @return a new {@link DependencyGraph} instance.
     */
    public static DependencyGraph read(String filename) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);

        IOGraph ioGraph;
        try {
            ioGraph = mapper.readValue(new File(filename), IOGraph.class);
        } catch (IOException e) {
            throw new IllegalStateException("Error reading from file: " + FileNameUtil.getCanonicalPath(filename), e);
        }

        return IO_GRAPH_MAPPER.createDependencyGraph(ioGraph);
    }
}
