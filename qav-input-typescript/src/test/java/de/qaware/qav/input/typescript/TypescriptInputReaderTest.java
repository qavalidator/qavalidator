package de.qaware.qav.input.typescript;

import de.qaware.qav.architecture.dsl.model.Architecture;
import de.qaware.qav.graph.api.DependencyGraph;
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
 * Tests for {@link TypescriptInputReader}
 */
public class TypescriptInputReaderTest {

    private DependencyGraph dependencyGraph;

    @Before
    public void setup() {
        this.dependencyGraph = DependencyGraphFactory.createGraph();
    }

    @Test
    public void read() {
        TypescriptInputReader reader = new TypescriptInputReader(dependencyGraph);
        reader.read("src/test/resources/typescript/ts-qav-export.xml");

        assertThat(dependencyGraph.getAllNodes().size()).isEqualTo(1588);
        assertThat(dependencyGraph.getAllEdges().size()).isGreaterThan(1588);

        Node n1 = dependencyGraph.getNode("src/dataModel/AliasNode.ts#AliasNode#original");
        assertThat(n1).isNotNull();
        assertThat(n1.getProperty("typescript-parent")).isEqualTo("src/dataModel/AliasNode.ts");
        Node n2 = dependencyGraph.getNode("src/dataModel/AliasNode.ts");
        assertThat(n2).isNotNull();
        assertThat(n2.getProperty("typescript-parent")).isNull();

        GraphExporter.export(dependencyGraph, "build/typescript-test/ts-qav-export", new Architecture(), new ArrayList<>(), false);
    }

    @Test
    public void testInitFileNotFound() {
        try {
            new TypescriptInputReader(dependencyGraph).read("not-existing-file.xml");
            fail("IllegalArgumentException expected");
        } catch(IllegalArgumentException e) {
            assertThat(e.getMessage()).startsWith("File not found: ");
        }
    }

    @Test
    public void testReadInvalidFile() {
        try {
            new TypescriptInputReader(dependencyGraph).read("src/test/resources/typescript/not-an-xml-file.noxml");
            fail("IllegalArgumentException expected");
        } catch(IllegalArgumentException e) {
            assertThat(e.getCause()).isInstanceOf(IOException.class);
            assertThat(e.getMessage()).startsWith("com.fasterxml.jackson.core.JsonParseException: Unexpected close tag </invalid-end-tag>; expected </start-tag>");
        }
    }
}