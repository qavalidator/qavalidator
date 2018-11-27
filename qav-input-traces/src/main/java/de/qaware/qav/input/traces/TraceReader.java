package de.qaware.qav.input.traces;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.qaware.qav.graph.api.Constants;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.Node;
import de.qaware.qav.input.traces.model.BinaryAnnotation;
import de.qaware.qav.input.traces.model.Span;
import de.qaware.qav.util.FileNameUtil;
import de.qaware.qav.util.FileSystemUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Read Zipkin traces and add them to a {@link DependencyGraph}.
 * <p>
 * Create a dependency graph from the spans, i.e. from the call relations that Zipkin observed.
 *
 * @author QAware GmbH
 */
@Slf4j
public class TraceReader {

    /** The value for the property "type" */
    public static final String TYPE_SPAN = "span";

    /** The value for the property "scope" */
    public static final String SCOPE_TRACE = "trace";

    /** The key to assign the service parent */
    public static final String TRACES_PARENT_KEY = "trace.parent";

    /** Assign this name if no other can be determined. */
    public static final String PLACEHOLDER_NAME = "<<empty>>";

    private final ObjectMapper mapper;
    private final DependencyGraph dependencyGraph;

    /**
     * Constructor.
     *
     * @param dependencyGraph the {@link DependencyGraph} where to add the nodes and edges
     */
    public TraceReader(DependencyGraph dependencyGraph) {
        this.dependencyGraph = dependencyGraph;
        this.mapper = new ObjectMapper();
    }

    /**
     * Read the Traces from the given file.
     *
     * @param filename the file to read
     * @throws IllegalArgumentException if the file does not exist.
     */
    public void read(String filename) {

        // check for existence
        if (!FileSystemUtil.checkFileOrResourceExists(filename)) {
            String msg = "File not found: " + FileNameUtil.getCanonicalPath(filename);
            LOGGER.error(msg);
            throw new IllegalArgumentException(msg);
        }

        // read the file
        try {
            // help the mapper to create the correct classes; it needs to know that this is a List of {@link Span}s.
            List<List<Span>> traces = mapper.readValue(new File(filename), new TypeReference<List<List<Span>>>() {
            });
            LOGGER.info("Read traces: {}", traces.size());

            analyzeAllTraces(traces);

        } catch (IOException e) {
            LOGGER.error("Can't read file {}", FileNameUtil.getCanonicalPath(filename), e);
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Iterate over the traces and analyze them
     *
     * @param traces the traces
     */
    private void analyzeAllTraces(List<List<Span>> traces) {
        traces.forEach(this::analyzeTrace);
    }

    /**
     * Analyze a single trace, which is a list of {@link Span}s.
     *
     * @param spans the list of {@link Span}s
     */
    private void analyzeTrace(List<Span> spans) {
        LOGGER.debug("Trace: {}, Spans: {}", spans.get(0).getTraceId(), spans.size());
        Map<String, Span> spanMap = new HashMap<>();
        spans.forEach(it -> spanMap.put(it.getId(), it));

        spans.forEach(span -> {
            String fromId = span.getParentId();
            Span parent = spanMap.get(fromId);
            addDependency(parent, span);
        });
    }

    /**
     * Add a dependency to the {@link #dependencyGraph}.
     *
     * @param fromSpan the start point
     * @param toSpan   the end point
     */
    private void addDependency(Span fromSpan, Span toSpan) {
        String fromEndpointName = fromSpan != null ? getEndpointUrl(fromSpan) : PLACEHOLDER_NAME;
        String toEndpointName = getEndpointUrl(toSpan);

        String fromServiceName = fromSpan != null ? getServiceName(fromSpan) : PLACEHOLDER_NAME;
        String toServiceName = getServiceName(toSpan);

        if (!fromEndpointName.equals(toEndpointName)) {
            LOGGER.debug("{} --> {}: {}", fromEndpointName, toEndpointName, toSpan);

            Node fromNode = getNode(fromEndpointName);
            if (!fromEndpointName.equals(fromServiceName)) {
                fromNode.setProperty(TRACES_PARENT_KEY, fromServiceName);
            }

            Node toNode = getNode(toEndpointName);
            if (!toEndpointName.equals(toServiceName)) {
                toNode.setProperty(TRACES_PARENT_KEY, toServiceName);
            }
            dependencyGraph.addDependency(fromNode, toNode, DependencyType.READ_WRITE);
        }
    }

    private String getEndpointUrl(Span span) {
        BinaryAnnotation annotation = findEntry(span.getBinaryAnnotations(), "http.path");
        if (annotation != null) {
            return annotation.getEndpoint().getServiceName() + ":/" + annotation.getValue();
        }

        // fallback:
        return getServiceName(span);
    }

    private String getServiceName(Span span) {
        if (span == null) {
            return PLACEHOLDER_NAME;
        }

        List<BinaryAnnotation> binaryAnnotations = span.getBinaryAnnotations();
        if (binaryAnnotations == null || binaryAnnotations.isEmpty()) {
            return "<" + span.getName() + ">";
        }

        BinaryAnnotation annotation = findEntry(span.getBinaryAnnotations(), "span.tag.service");
        if (annotation == null) {
            int idx = binaryAnnotations.size() == 1 ? 0 : 1;
            annotation = binaryAnnotations.get(idx);
        }
        return annotation.getEndpoint().getServiceName();

    }

    private BinaryAnnotation findEntry(List<BinaryAnnotation> annotations, String key) {
        if (annotations == null) {
            return null;
        }
        return annotations.stream()
                .filter(annotation -> annotation.getKey().equals(key))
                .findFirst()
                .orElse(null);
    }

    /**
     * Get the node for the given name.
     * <p>
     * Creates it, if it does not exist. Sets properties on the node, if it is new.
     *
     * @param name name of the node
     * @return the {@link Node}
     */
    private Node getNode(String name) {
        Node result = dependencyGraph.getNode(name);
        if (result == null) {
            result = dependencyGraph.getOrCreateNodeByName(name);
            result.setProperty(Constants.TYPE, TYPE_SPAN);
            result.setProperty(Constants.SCOPE, SCOPE_TRACE);
        }
        return result;
    }
}
