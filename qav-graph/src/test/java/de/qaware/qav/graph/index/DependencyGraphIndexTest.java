package de.qaware.qav.graph.index;

import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.Node;
import de.qaware.qav.graph.impl.DependencyGraphSimpleImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;

/**
 * @author QAware GmbH
 */
public class DependencyGraphIndexTest {

    private DependencyGraph graph;
    private DependencyGraphIndex index;
    private Node v1;
    private Node v2;
    private Node v3;
    private Node v4;
    private Node v5;
    private Node v6;

    @Before
    public void setup() {
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
        assertThat(nodes, hasSize(1));
        assertThat(nodes, contains(v1));

        nodes = index.findNodes("x:yellow");
        assertThat(nodes, hasSize(3));
        assertThat(nodes, containsInAnyOrder(v3, v4, v5));
    }

    @Test
    public void findNodesWithNumericFields() {
        Set<Node> nodes = index.findNodes("size:2");
        assertThat(nodes, hasSize(1));
        assertThat(nodes, contains(v2));
    }

    @Test
    public void findNodesWithNumericFieldMultipleHits() {
        Set<Node> nodes = index.findNodes("size:250");
        assertThat(nodes, hasSize(2));
        assertThat(nodes, containsInAnyOrder(v5, v6));
    }

    @Test
    public void findNodesWithNumericRange() {
        Set<Node> nodes = index.findNodes("size:[80 TO 200]");
        assertThat(nodes, hasSize(2));
        assertThat(nodes, containsInAnyOrder(v3, v4));
    }

}