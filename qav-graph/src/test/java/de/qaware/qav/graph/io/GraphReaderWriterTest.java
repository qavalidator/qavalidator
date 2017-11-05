package de.qaware.qav.graph.io;

import com.google.common.collect.Lists;
import de.qaware.qav.graph.api.Dependency;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.Node;
import de.qaware.qav.graph.factory.DependencyGraphFactory;
import de.qaware.qav.util.FileSystemUtil;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

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

        assertThat(readGraph.getAllNodes().size(), is(graph.getAllNodes().size()));
        assertThat(readGraph.getAllEdges().size(), is(graph.getAllEdges().size()));
        assertThat(readGraph.getEdge(readGraph.getNode("v1"), readGraph.getNode("v3")).getProperty("prop1"), is(1));
        assertThat(readGraph.getEdge(readGraph.getNode("v1"), readGraph.getNode("v3")).getProperty("prop2"), is("Two"));
        Node v1 = readGraph.getNode("v1");
        assertThat(v1, notNullValue());
        assertThat(v1.getProperty("list-key-1"), is(Lists.newArrayList("a", "b", "c")));
        assertThat(v1.getProperty("list-key-2"), is(Lists.newArrayList(1, 2, 3)));

        GraphReaderWriter.write(readGraph, "build/testGraph2.json");

        String s1 = FileSystemUtil.readFileAsText("build/testGraph1.json");
        String s2 = FileSystemUtil.readFileAsText("build/testGraph2.json");

        assertThat(s1, is(s2));
    }

}