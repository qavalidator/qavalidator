package de.qaware.qav.input.traces;

import com.fasterxml.jackson.core.JsonParser;
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
    public static final String SCOPE_TRACE_ENDPOINT = "trace.endpoint";

    /** The value for the property "scope" */
    public static final String SCOPE_TRACE_SERVICE = "trace.service";

    /** The key to assign the service parent */
    public static final String TRACES_PARENT_KEY = "trace.parent";

    /** Assign this name if no other can be determined. */
    public static final String PLACEHOLDER_NAME = "__EMPTY__";

    private final ObjectMapper mapper;
    private final DependencyGraph dependencyGraph;

    /**
     * Constructor.
     *
     * @param dependencyGraph the {@link DependencyGraph} where to add the nodes and edges
     */
    public TraceReader(DependencyGraph dependencyGraph) {
        this.dependencyGraph = dependencyGraph;
        this.mapper = new ObjectMapper().enable(JsonParser.Feature.ALLOW_COMMENTS);
    }

    /**
     * Read the Traces from the given file.
     *
     * @param filename the file to read
     * @throws IllegalArgumentException if the file does not exist.
     */
    public void read(String filename) {
        FileSystemUtil.assertFileOrResourceExists(filename);

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
     * <p>
     * Adds two dependencies: one on the level of endpoint URLs, and the other on the level of services.
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

            // add the nodes and the hierarchy relations:
            Node fromEndpointNode = getNode(fromEndpointName, SCOPE_TRACE_ENDPOINT);
            Node fromServiceNode = getNode(fromServiceName, SCOPE_TRACE_SERVICE);
            addContains(fromServiceNode, fromEndpointNode);

            Node toEndpointNode = getNode(toEndpointName, SCOPE_TRACE_ENDPOINT);
            Node toServiceNode = getNode(toServiceName, SCOPE_TRACE_SERVICE);
            addContains(toServiceNode, toEndpointNode);

            // add dependencies on endpoint level:
            dependencyGraph.addDependency(fromEndpointNode, toEndpointNode, DependencyType.READ_WRITE);

            // add dependencies on service level:
            if (!fromServiceName.equals(toServiceName)) {
                dependencyGraph.addDependency(fromServiceNode, toServiceNode, DependencyType.READ_WRITE);
            }
        }
    }

    /**
     * Get the endpoint name.
     * <p>
     * Concatenates the service name with the REST URL. If it can't identify the REST URL, then it falls back to the
     * {@link #getServiceName(Span)} method.
     *
     * @param span the {@link Span}
     * @return the endpoint name
     */
    private String getEndpointUrl(Span span) {
        BinaryAnnotation annotation = findEntry(span.getBinaryAnnotations(), "http.path");
        if (annotation != null) {
            return annotation.getEndpoint().getServiceName() + ":/" + annotation.getValue();
        }

        // fallback:
        return getServiceName(span);
    }

    /**
     * Get the service name.
     * <p>
     * It's the service name as Zipkin has it in the endpoint description.
     *
     * @param span the {@link Span}
     * @return the service name
     */
    private String getServiceName(Span span) {
        List<BinaryAnnotation> binaryAnnotations = span.getBinaryAnnotations();
        if (binaryAnnotations == null || binaryAnnotations.isEmpty()) {
            return "[" + span.getName() + "]";
        }

        BinaryAnnotation annotation = findEntry(span.getBinaryAnnotations(), "span.tag.service");

        // fallback: just take anything that could be a sensible service name
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
     * Create hierarchy relation between service nodes and endpoints nodes.
     *
     * @param serviceNode  the service (will be parent)
     * @param endpointNode the endpoint (will be child)
     */
    private void addContains(Node serviceNode, Node endpointNode) {
        if (!endpointNode.getName().equals(serviceNode.getName())) {
            endpointNode.setProperty(TRACES_PARENT_KEY, serviceNode.getName());
            dependencyGraph.addDependency(serviceNode, endpointNode, DependencyType.CONTAINS);
        }
    }

    /**
     * Get the node for the given name.
     * <p>
     * Creates it, if it does not exist. Sets properties on the node, if it is new.
     *
     * @param name      name of the node
     * @param scopeName name of the scope where the node belongs
     * @return the {@link Node}
     */
    private Node getNode(String name, String scopeName) {
        Node result = dependencyGraph.getOrCreateNodeByName(name);
        result.setProperty(Constants.TYPE, TYPE_SPAN);
        result.addListProperty(Constants.SCOPE, scopeName);
        return result;
    }
}
