package de.qaware.qav.input.traces;

import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.Node;
import de.qaware.qav.graph.factory.DependencyGraphFactory;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

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

        Node n1 = dependencyGraph.getNode("<<empty>>");
        assertThat(n1).isNotNull();
        Node n2 = dependencyGraph.getNode("kong");
        assertThat(n2).isNotNull();

        assertThat(dependencyGraph.getEdge(n1, n2)).isNotNull();
    }
}