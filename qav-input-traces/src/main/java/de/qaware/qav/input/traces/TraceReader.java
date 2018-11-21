package de.qaware.qav.input.traces;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
        LOGGER.info("Trace: {}, Spans: {}", spans.get(0).getTraceId(), spans.size());
        Map<String, Span> spanMap = new HashMap<>();
        spans.forEach(it -> spanMap.put(it.getId(), it));

        for (Span span : spans) {
            String fromName = "<<empty>>";
            String fromId = span.getParentId();
            Span parent = spanMap.get(fromId);
            if (parent != null) {
                fromName = getServiceName(parent);
            }
            String toName = getServiceName(span);
            addDependency(fromName, toName, span);
        }
    }

    private String getServiceName(Span span) {
        if (span == null) {
            return "<<empty>>";
        }

        List<BinaryAnnotation> binaryAnnotations = span.getBinaryAnnotations();
        if (binaryAnnotations == null || binaryAnnotations.isEmpty()) {
            return "<" + span.getName() + ">";
        }

        int idx = 1;
        if (binaryAnnotations.size() == 1) {
            idx = 0;
        }

        return binaryAnnotations.get(idx).getEndpoint().getServiceName();
    }

    /**
     * Add the dependency to the {@link DependencyGraph}.
     *
     * @param fromName source name
     * @param toName   target name
     * @param span     the span; may be used to derive further properties
     */
    private void addDependency(String fromName, String toName, Span span) {
        LOGGER.info("{} --> {}: {}", fromName, toName, span);

        if (!fromName.equals(toName)) {
            Node fromNode = dependencyGraph.getOrCreateNodeByName(fromName);
            Node toNode = dependencyGraph.getOrCreateNodeByName(toName);
            dependencyGraph.addDependency(fromNode, toNode, DependencyType.READ_WRITE);
        }
    }
}
