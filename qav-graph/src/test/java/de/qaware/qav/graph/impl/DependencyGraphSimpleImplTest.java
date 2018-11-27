package de.qaware.qav.graph.impl;

import de.qaware.qav.graph.api.Dependency;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.Node;
import de.qaware.qav.graph.filter.DependencyTypeEdgeOutFilter;
import de.qaware.qav.graph.filter.NodeNameInFilter;
import de.qaware.qav.graph.filter.NotFilter;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test for {@link DependencyGraphSimpleImpl}
 */
public class DependencyGraphSimpleImplTest {

    private DependencyGraphSimpleImpl graph;
    private Node n1;
    private Node n2;
    private Node n3;

    @Before
    public void setup() {
        graph = new DependencyGraphSimpleImpl();
    }

    /**
     * tests: getOrCreateNodeByName, hasNode()
     */
    @Test
    public void testGetOrCreateNodeByName() {
        Node node = new Node("x");
        assertThat(graph.hasNode("x"), is(false));

        Node x = graph.getOrCreateNodeByName("x");
        assertEquals(node, x);
        assertNotSame(node, x);
        assertThat(graph.hasNode("x"), is(true));

        // will not be created a second time:
        Node y = graph.getOrCreateNodeByName("x");
        assertSame(x, y);
    }

    /**
     * tests: inserting a dependency, replacing it with a "higher" or "stronger" dependency, not replacing it with a
     * "lower" or "weaker" dependency.
     */
    @Test
    public void testAddDependency() {
        Node n1 = graph.getOrCreateNodeByName("v1");
        Node n2 = graph.getOrCreateNodeByName("v2");
        assertNull(graph.getEdge(n1, n2));

        assertThat(graph.getAllNodes(), hasSize(2));
        assertThat(graph.getAllEdges(), hasSize(0));

        graph.addDependency(n1, n2, DependencyType.READ_ONLY);

        assertThat(graph.getAllEdges(), hasSize(1));
        assertNotNull(graph.getEdge(n1, n2));
        assertNull(graph.getEdge(n2, n1));

        Dependency dep = graph.getAllEdges().iterator().next();
        assertTrue(dep.getSource().equals(n1));
        assertTrue(dep.getTarget().equals(n2));
        assertTrue(dep.getDependencyType().equals(DependencyType.READ_ONLY));

        // Test overwriting the dependency type:
        graph.addDependency(n1, n2, DependencyType.CREATE); // type is "higher", old dependency will be changed

        assertThat(graph.getAllEdges(), hasSize(1)); // still 1! no additional dependency.
        dep = graph.getAllEdges().iterator().next();
        assertTrue(dep.getSource().equals(n1));
        assertTrue(dep.getTarget().equals(n2));
        assertTrue(dep.getDependencyType().equals(DependencyType.CREATE));

        // but it won't replace a "lower" dependency:
        graph.addDependency(n1, n2, DependencyType.READ_WRITE); // type is "lower", no change

        assertThat(graph.getAllEdges(), hasSize(1)); // still 1! no additional dependency.
        dep = graph.getAllEdges().iterator().next();
        assertTrue(dep.getSource().equals(n1));
        assertTrue(dep.getTarget().equals(n2));
        assertTrue(dep.getDependencyType().equals(DependencyType.CREATE)); // still the higher dependency.
    }

    @Test
    public void testGetIncomingAndOutgoingEdges() {
        setupSimpleGraph();

        assertThat(graph.getIncomingEdges(n1), hasSize(0));
        assertThat(graph.getOutgoingEdges(n1), hasSize(2));

        assertThat(graph.getIncomingEdges(n2), hasSize(1));
        assertTrue(graph.getOutgoingEdges(n2).isEmpty());
    }

    @Test
    public void testGetIncomingAndOutgoingEdgesWithTypes() {
        setupSimpleGraph();

        Set<Dependency> outgoingCreate = graph.getOutgoingEdges(n1, DependencyType.CREATE);
        assertThat(outgoingCreate, hasSize(1));
        assertThat(outgoingCreate.iterator().next().getTarget(), is(n3));

        Set<Dependency> incomingCreate = graph.getIncomingEdges(n1, DependencyType.CREATE);
        assertThat(incomingCreate, notNullValue());
        assertTrue(incomingCreate.isEmpty());
    }

    @Test
    public void testFilter() {
        setupSimpleGraph();
        Node n4 = graph.getOrCreateNodeByName("v4");
        Node n5 = graph.getOrCreateNodeByName("v5");
        graph.addDependency(n2, n4, DependencyType.READ_WRITE);
        graph.addDependency(n2, n5, DependencyType.READ_WRITE);

        DependencyGraph filteredGraph = graph.filter(new NotFilter(new NodeNameInFilter("v2", "v5")));
        assertThat(graph.getNode("v1"), is(new Node("v1")));
        assertThat(graph.getNode("v2"), is(new Node("v2")));
        assertThat(graph.getNode("v3"), is(new Node("v3")));
        assertThat(graph.getNode("v4"), is(n4));
        assertThat(graph.getNode("v5"), is(n5));
        assertThat(graph.getNode("v6"), nullValue());

        assertThat(filteredGraph.getNode("v1"), is(new Node("v1")));
        assertThat(filteredGraph.getNode("v2"), nullValue());
        assertThat(filteredGraph.getNode("v3"), is(new Node("v3")));
        assertThat(filteredGraph.getNode("v4"), is(new Node("v4")));
        assertThat(filteredGraph.getNode("v5"), nullValue());

        assertThat(filteredGraph.getEdge(n1, n3), notNullValue()); // both nodes are in the filtered graph
        assertThat(graph.getEdge(n1, n2), notNullValue()); // edge exists
        assertThat(filteredGraph.getEdge(n1, n2), nullValue()); // but invisible in the filtered graph because target node is filtered
        assertThat(graph.getEdge(n2, n4), notNullValue()); // edge exists
        assertThat(filteredGraph.getEdge(n2, n4), nullValue()); // but invisible because source node is filtered
        assertThat(graph.getEdge(n2, n5), notNullValue()); // edge exists
        assertThat(filteredGraph.getEdge(n2, n5), nullValue()); // but invisible because both nodes are filtered

        try {
            filteredGraph.getOrCreateNodeByName("v6");
            fail("filtered graph is unmodifiable.");
        } catch (UnsupportedOperationException e) {
            assertThat(e.getMessage(), is("this graph is unmodifiable"));
        }

        assertThat(graph, is(not(filteredGraph)));
        assertThat(graph.getBaseGraph(), is(graph));
        assertThat(filteredGraph.getBaseGraph(), is(graph));
    }

    @Test
    public void testFilterMultipleTimes() {
        setupSimpleGraph();

        DependencyGraph filteredGraph = graph.filter(new NotFilter(new NodeNameInFilter("v2", "v5")));
        assertThat(graph.getNode("v1"), is(new Node("v1")));
        assertThat(graph.getNode("v2"), is(new Node("v2")));
        assertThat(graph.getNode("v3"), is(new Node("v3")));

        assertThat(filteredGraph.getNode("v1"), is(new Node("v1")));
        assertThat(filteredGraph.getNode("v2"), nullValue());
        assertThat(filteredGraph.getNode("v3"), is(new Node("v3")));

        DependencyGraph secondFilteredGraph = filteredGraph.filter(new NotFilter(new NodeNameInFilter("v4")));

        assertThat(secondFilteredGraph.getNode("v3"), notNullValue());
        assertThat(secondFilteredGraph.getNode("v4"), nullValue());
        assertThat(secondFilteredGraph.getBaseGraph(), is(graph));
    }

    @Test
    public void testEdgeFilter() {
        setupSimpleGraph();

        DependencyGraph filteredGraph = graph.filter(new DependencyTypeEdgeOutFilter(DependencyType.READ_ONLY));
        assertThat(graph.getNode("v1"), is(new Node("v1")));
        assertThat(graph.getNode("v2"), is(new Node("v2")));
        assertThat(graph.getNode("v3"), is(new Node("v3")));

        assertThat(filteredGraph.getNode("v1"), is(new Node("v1")));
        assertThat(filteredGraph.getNode("v2"), is(new Node("v2")));
        assertThat(filteredGraph.getNode("v3"), is(new Node("v3")));

        assertThat(graph.getAllEdges(), hasSize(2));
        assertThat(filteredGraph.getAllEdges(), hasSize(1));
        assertThat(filteredGraph.getIncomingEdges(n2), hasSize(0));
        assertThat(filteredGraph.getIncomingEdges(n3), hasSize(1));
    }

    @Test
    public void testToString() {
        setupSimpleGraph();

        assertThat(graph.toString(), is("DependencyGraphSimpleImpl[Nodes:3; Edges:2]"));
    }

    private void setupSimpleGraph() {
        n1 = graph.getOrCreateNodeByName("v1");
        n2 = graph.getOrCreateNodeByName("v2");
        n3 = graph.getOrCreateNodeByName("v3");
        graph.addDependency(n1, n2, DependencyType.READ_ONLY);
        graph.addDependency(n1, n3, DependencyType.CREATE);
    }
}