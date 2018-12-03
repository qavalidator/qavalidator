package de.qaware.qav.input.traces;

import com.google.common.collect.Lists;
import de.qaware.qav.architecture.dsl.model.Architecture;
import de.qaware.qav.graph.api.Constants;
import de.qaware.qav.graph.api.Dependency;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.Node;
import de.qaware.qav.graph.factory.DependencyGraphFactory;
import de.qaware.qav.visualization.GraphExporter;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * Tests for {@link TraceReader}.
 *
 * @author QAware GmbH
 */
public class TraceReaderTest {

    private TraceReader traceReader;
    private DependencyGraph dependencyGraph;

    @Before
    public void setup() {
        dependencyGraph = DependencyGraphFactory.createGraph();
        this.traceReader = new TraceReader(dependencyGraph);
    }

    @Test
    public void testNotExistingFile() {
        try {
            traceReader.read("/not/existing/file");
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).startsWith("File not found: ");
        }
    }

    @Test
    public void testNoJsonFile() {
        try {
            traceReader.read("src/test/resources/noJsonFile.txt");
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e.getCause()).isInstanceOf(IOException.class);
            assertThat(e.getMessage()).startsWith("com.fasterxml.jackson.core.JsonParseException: Unrecognized token 'This': was expecting ('true', 'false' or 'null')");
        }
    }

    @Test
    public void testBasics() {
        traceReader.read("src/test/resources/basicTrace.json");
        assertThat(dependencyGraph.getAllNodes()).hasSize(2);

        Node n1 = dependencyGraph.getNode("__EMPTY__");
        assertThat(n1).isNotNull();
        assertThat(n1.hasProperty(Constants.TYPE)).isTrue();
        assertThat(n1.getProperty(Constants.TYPE)).isEqualTo(TraceReader.TYPE_SPAN);
        assertThat(n1.hasProperty(Constants.SCOPE)).isTrue();
        assertThat(n1.getProperty(Constants.SCOPE)).isEqualTo(Lists.newArrayList(TraceReader.SCOPE_TRACE_ENDPOINT, TraceReader.SCOPE_TRACE_SERVICE));

        Node n2 = dependencyGraph.getNode("kong");
        assertThat(n2).isNotNull();

        Dependency edge = dependencyGraph.getEdge(n1, n2);
        assertThat(edge).isNotNull();
        assertThat(edge.getDependencyType()).isEqualTo(DependencyType.READ_WRITE);
    }

    @Test
    public void testTrace2() {
        traceReader.read("src/test/resources/trace2.json");
        assertThat(dependencyGraph.getAllNodes()).hasSize(6);

        Node n1 = dependencyGraph.getNode("__EMPTY__");
        assertThat(n1).isNotNull();
        assertThat(n1.hasProperty(Constants.TYPE)).isTrue();
        assertThat(n1.getProperty(Constants.TYPE)).isEqualTo(TraceReader.TYPE_SPAN);
        assertThat(n1.hasProperty(Constants.SCOPE)).isTrue();
        assertThat(n1.getProperty(Constants.SCOPE)).isEqualTo(Lists.newArrayList(TraceReader.SCOPE_TRACE_ENDPOINT, TraceReader.SCOPE_TRACE_SERVICE));

        GraphExporter.export(dependencyGraph, "build/test-output/traces2", new Architecture(), new ArrayList<>(), true);
    }

    @Test
    public void testTraceDegeneratedTrace1() {
        traceReader.read("src/test/resources/degeneratedTrace1.json");
        assertThat(dependencyGraph.getAllNodes()).hasSize(2);

        Node n1 = dependencyGraph.getNode("__EMPTY__");
        assertThat(n1).isNotNull();
    }

    @Test
    public void testTraceDegeneratedTrace2() {
        traceReader.read("src/test/resources/degeneratedTrace2.json");
        assertThat(dependencyGraph.getAllNodes()).hasSize(2);

        Node n1 = dependencyGraph.getNode("__EMPTY__");
        assertThat(n1).isNotNull();
    }
}