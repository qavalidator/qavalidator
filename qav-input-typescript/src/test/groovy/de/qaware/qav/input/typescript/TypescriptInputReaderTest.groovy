package de.qaware.qav.input.typescript

import de.qaware.qav.architecture.dsl.model.Architecture
import de.qaware.qav.graph.api.DependencyGraph
import de.qaware.qav.graph.factory.DependencyGraphFactory
import de.qaware.qav.visualization.GraphExporter
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.fail

/**
 * Tests for {@link TypescriptInputReader}.
 *
 * @author QAware GmbH
 */
class TypescriptInputReaderTest {

    DependencyGraph dependencyGraph

    @Before
    void setup() {
        this.dependencyGraph = DependencyGraphFactory.createGraph()
    }

    @Test
    void read() {
        TypescriptInputReader reader = new TypescriptInputReader(dependencyGraph)
        reader.read("src/test/resources/typescript/ts-qav-export.xml")

        assert dependencyGraph.getAllNodes().size() == 1588
        assert dependencyGraph.getAllEdges().size() >= 1588

        assert dependencyGraph.getNode("src/dataModel/AliasNode.ts#AliasNode#original").getProperty("typescript-parent") == "src/dataModel/AliasNode.ts"
        assert dependencyGraph.getNode("src/dataModel/AliasNode.ts").getProperty("typescript-parent") == null

        GraphExporter.export(dependencyGraph, "build/typescript-test/ts-qav-export", new Architecture(), [], false)
    }

    @Test
    void testInitFileNotFound() {
        try {
            new TypescriptInputReader(dependencyGraph).read("not-existing-file.xml")
            fail("IllegalArgumentException expected")
        } catch(IllegalArgumentException e) {
            assert e.getMessage().contains("not-existing-file.xml does not exist")
        }
    }
}
