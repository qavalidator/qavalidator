package de.qaware.qav.graph.io;

import com.google.common.collect.Lists;
import de.qaware.qav.graph.api.Dependency;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.Node;
import de.qaware.qav.graph.factory.DependencyGraphFactory;
import de.qaware.qav.util.FileSystemUtil;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Tests for {@link GraphReaderWriter}.
 *
 * @author QAware GmbH
 */
public class GraphReaderWriterTest {

    @Test
    public void testIO() {
        DependencyGraph graph = DependencyGraphFactory.createGraph();

        Node n1 = graph.getOrCreateNodeByName("v1");
        n1.setProperty("asdf", true);
        n1.setProperty("KEY", 5);
        n1.addListProperty("list-key-1", "a");
        n1.addListProperty("list-key-1", "b");
        n1.addListProperty("list-key-1", "c");
        n1.addListProperty("list-key-2", 1);
        n1.addListProperty("list-key-2", 2);
        n1.addListProperty("list-key-2", 3);
        Node n2 = graph.getOrCreateNodeByName("v2");
        Node n3 = graph.getOrCreateNodeByName("v3");
        Node n4 = graph.getOrCreateNodeByName("v4");
        Node n5 = graph.getOrCreateNodeByName("v5");
        graph.addDependency(n1, n2, DependencyType.READ_ONLY);
        Dependency dep13 = graph.addDependency(n1, n3, DependencyType.CREATE);
        Dependency dep45 = graph.addDependency(n4, n5, DependencyType.READ_WRITE);
        dep45.setProperty("lineNo", 45);
        dep13.addBaseDependency(dep45);
        dep13.setProperty("prop1", 1);
        dep13.setProperty("prop2", "Two");

        GraphReaderWriter.write(graph, "build/testGraph1.json");
        DependencyGraph readGraph = GraphReaderWriter.read("build/testGraph1.json");

        assertThat(readGraph.getAllNodes()).hasSameSizeAs(graph.getAllNodes());
        assertThat(readGraph.getAllEdges()).hasSameSizeAs(graph.getAllEdges());
        assertThat(readGraph.getEdge(readGraph.getNode("v1"), readGraph.getNode("v3")).getProperty("prop1")).isEqualTo(1);
        assertThat(readGraph.getEdge(readGraph.getNode("v1"), readGraph.getNode("v3")).getProperty("prop2")).isEqualTo("Two");
        Node v1 = readGraph.getNode("v1");
        assertThat(v1).isNotNull();
        assertThat(v1.getProperty("list-key-1")).isEqualTo(Lists.newArrayList("a", "b", "c"));
        assertThat(v1.getProperty("list-key-2")).isEqualTo(Lists.newArrayList(1, 2, 3));

        GraphReaderWriter.write(readGraph, "build/testGraph2.json");

        String s1 = FileSystemUtil.readFileAsText("build/testGraph1.json");
        String s2 = FileSystemUtil.readFileAsText("build/testGraph2.json");

        assertThat(s1).isEqualTo(s2);
    }

    /**
     * merging the same graph does not change anything
     */
    @Test
    public void testMergeSame() {
        DependencyGraph graph = DependencyGraphFactory.createGraph();

        GraphReaderWriter.merge(graph, "src/test/resources/graphs/testGraph1.json");
        assertThat(graph.getAllNodes()).hasSize(5);

        GraphReaderWriter.merge(graph, "src/test/resources/graphs/testGraph1.json");
        assertThat(graph.getAllNodes()).hasSize(5);
    }

    /**
     * merging two graphs without overlap
     */
    @Test
    public void testMergeNoOverlap() {
        DependencyGraph graph = DependencyGraphFactory.createGraph();

        GraphReaderWriter.merge(graph, "src/test/resources/graphs/testGraph1.json");
        assertThat(graph.getAllNodes()).hasSize(5);

        GraphReaderWriter.merge(graph, "src/test/resources/graphs/testGraph2.json");
        assertThat(graph.getAllNodes()).hasSize(10);
    }

    @Test
    public void testMergeWithOverlap() {
        DependencyGraph graph = DependencyGraphFactory.createGraph();

        GraphReaderWriter.merge(graph, "src/test/resources/graphs/testGraph1.json");
        assertThat(graph.getAllNodes()).hasSize(5);
        assertThat(graph.getNode("v1").getProperty("list-key-1")).isEqualTo(Lists.newArrayList("a", "b", "c"));
        assertThat(graph.getNode("v1").getProperty("KEY")).isEqualTo(5);

        // look into graph3:
        DependencyGraph graph3 = DependencyGraphFactory.createGraph();
        GraphReaderWriter.merge(graph3, "src/test/resources/graphs/testGraph3.json");
        assertThat(graph3.getAllNodes()).hasSize(2);
        assertThat(graph3.getNode("v1")).isNotNull();
        assertThat(graph3.getNode("v1").getProperty("list-key-1")).isEqualTo(Lists.newArrayList("c", "d"));
        assertThat(graph3.getNode("v6")).isNotNull();
        assertThat(graph3.getNode("v1").getProperty("KEY")).isEqualTo(6);

        // now merge graph 3 into graph 1:
        GraphReaderWriter.merge(graph, "src/test/resources/graphs/testGraph3.json");
        assertThat(graph.getAllNodes()).hasSize(6);
        assertThat(graph.getNode("v1").getProperty("list-key-1")).isEqualTo(Lists.newArrayList("a", "b", "c", "d"));
        assertThat(graph.getNode("v1").getProperty("KEY")).isEqualTo(Lists.newArrayList(5, 6));
    }

}