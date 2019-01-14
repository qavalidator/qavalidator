package de.qaware.qav.graph.index;

import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.Node;
import de.qaware.qav.graph.impl.DependencyGraphSimpleImpl;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * Tests for {@link DependencyGraphIndex}.
 */
public class DependencyGraphIndexTest {

    private static DependencyGraph graph;
    private static DependencyGraphIndex index;
    private static Node v1;
    private static Node v2;
    private static Node v3;
    private static Node v4;
    private static Node v5;
    private static Node v6;

    @BeforeClass
    public static void setup() {
        graph = new DependencyGraphSimpleImpl(graph);

        v1 = graph.getOrCreateNodeByName("v1");
        v2 = graph.getOrCreateNodeByName("v2");
        v3 = graph.getOrCreateNodeByName("v3");
        v4 = graph.getOrCreateNodeByName("v4");
        v5 = graph.getOrCreateNodeByName("v5");
        v6 = graph.getOrCreateNodeByName("v6");

        v1.setProperty("size", "1");
        v2.setProperty("size", "2");
        v3.setProperty("size", "100");
        v4.setProperty("size", "150");
        v5.setProperty("size", "250");
        v6.setProperty("size", "250");

        v1.setProperty("x", "green");
        v2.setProperty("x", "blue");
        v3.setProperty("x", "yellow");
        v4.setProperty("x", "yellow");
        v5.setProperty("x", "yellow");
        v6.setProperty("x", "blue");

        index = new DependencyGraphIndex(graph, "size");
    }

    @Test
    public void findNodes() {
        Set<Node> nodes = index.findNodes("x:green");
        assertThat(nodes).hasSize(1);
        assertThat(nodes).contains(v1);

        nodes = index.findNodes("x:yellow");
        assertThat(nodes).hasSize(3);
        assertThat(nodes).contains(v3, v4, v5);
    }

    @Test
    public void findNodesWithNumericFields() {
        Set<Node> nodes = index.findNodes("size:2");
        assertThat(nodes).hasSize(1);
        assertThat(nodes).contains(v2);
    }

    @Test
    public void findNodesWithNumericFieldMultipleHits() {
        Set<Node> nodes = index.findNodes("size:250");
        assertThat(nodes).hasSize(2);
        assertThat(nodes).contains(v5, v6);
    }

    @Test
    public void findNodesWithNumericRange() {
        Set<Node> nodes = index.findNodes("size:[80 TO 200]");
        assertThat(nodes).hasSize(2);
        assertThat(nodes).contains(v3, v4);
    }

    @Test
    public void invalidQuery() {
        try {
            index.findNodes("invalid:");
            fail("IllegalArgumentException expected");
        } catch(IllegalArgumentException e) {
            assertThat(e.getMessage()).startsWith("Parsing of query failed: invalid:");
        }
    }

}