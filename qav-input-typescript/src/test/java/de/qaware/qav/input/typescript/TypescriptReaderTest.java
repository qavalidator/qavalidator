package de.qaware.qav.input.typescript;

import de.qaware.qav.architecture.dsl.model.Architecture;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.Node;
import de.qaware.qav.graph.factory.DependencyGraphFactory;
import de.qaware.qav.graph.io.GraphReaderWriter;
import de.qaware.qav.visualization.GraphExporter;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * Tests for {@link TypescriptReader}
 */
public class TypescriptReaderTest {

    private DependencyGraph dependencyGraph;

    @Before
    public void setup() {
        this.dependencyGraph = DependencyGraphFactory.createGraph();
    }

    @Test
    public void read() {
        TypescriptReader reader = new TypescriptReader(dependencyGraph);
        reader.read("src/test/resources/typescript/ts-qav-export.xml");

        assertThat(dependencyGraph.getAllNodes().size()).isEqualTo(1588);
        assertThat(dependencyGraph.getAllEdges().size()).isGreaterThan(1588);

        Node n1 = dependencyGraph.getNode("src/dataModel/AliasNode.ts#AliasNode#original");
        assertThat(n1).isNotNull();
        assertThat(n1.getProperty("typescript-parent")).isEqualTo("src/dataModel/AliasNode.ts");
        Node n2 = dependencyGraph.getNode("src/dataModel/AliasNode.ts");
        assertThat(n2).isNotNull();
        assertThat(n2.getProperty("typescript-parent")).isNull();

        GraphExporter.export(dependencyGraph, "build/typescript-test/ts-qav-export_2", new Architecture(), new ArrayList<>(), false);
        GraphReaderWriter.write(dependencyGraph, "build/typescript-test/ts-qav-export_2.json");
    }

    @Test
    public void testInitFileNotFound() {
        try {
            new TypescriptInputReader(dependencyGraph).read("not-existing-file.xml");
            fail("IllegalArgumentException expected");
        } catch(IllegalArgumentException e) {
            assertThat(e.getMessage()).contains("not-existing-file.xml does not exist");
        }
    }
}