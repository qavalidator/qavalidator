package de.qaware.qav.graph.alg;

import de.qaware.qav.graph.alg.api.CycleFinder;
import de.qaware.qav.graph.alg.impl.CycleFinderImpl;
import de.qaware.qav.graph.api.Dependency;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.Node;
import de.qaware.qav.graph.factory.DependencyGraphFactory;
import de.qaware.qav.graph.api.EdgeFilter;
import de.qaware.qav.graph.api.NodeFilter;
import org.junit.Test;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Test the cycle finder.
 */
public class CycleFinderImplTest {

    @Test
    public void testHasNoCycle() {
        DependencyGraph graph = DependencyGraphFactory.createGraph();

        Node v1 = graph.getOrCreateNodeByName("v1");
        Node v2 = graph.getOrCreateNodeByName("v2");
        Node v3 = graph.getOrCreateNodeByName("v3");

        graph.addDependency(v1, v2, DependencyType.READ_ONLY);
        graph.addDependency(v2, v3, DependencyType.READ_ONLY);
        graph.addDependency(v1, v3, DependencyType.READ_ONLY);

        CycleFinder cf = new CycleFinderImpl(graph);
        assertFalse(cf.hasCycles());
        assertTrue(cf.getCycles().isEmpty());
    }

    @Test
    public void testHasOneCycle() {
        DependencyGraph graph = DependencyGraphFactory.createGraph();

        Node v1 = graph.getOrCreateNodeByName("v1");
        Node v2 = graph.getOrCreateNodeByName("v2");
        Node v3 = graph.getOrCreateNodeByName("v3");
        Node v4 = graph.getOrCreateNodeByName("v4");

        graph.addDependency(v1, v2, DependencyType.READ_ONLY);
        graph.addDependency(v2, v3, DependencyType.READ_ONLY);
        graph.addDependency(v3, v1, DependencyType.READ_ONLY);
        graph.addDependency(v4, v1, DependencyType.READ_ONLY); // v4 not part of the cycle.

        CycleFinder cf = new CycleFinderImpl(graph);
        assertTrue(cf.hasCycles());
        assertThat(cf.getCycles().size(), is(1));
        List<Node> cycle = cf.getCycles().get(0);
        assertThat(cycle.size(), is(3));

        assertThat(cycle.contains(v1), is(true));
        assertThat(cycle.contains(v2), is(true));
        assertThat(cycle.contains(v3), is(true));
        assertThat(cycle.contains(v4), is(false));
    }

    @Test
    public void testHasTwoCycles() {
        DependencyGraph graph = DependencyGraphFactory.createGraph();

        Node v1 = graph.getOrCreateNodeByName("v1");
        Node v2 = graph.getOrCreateNodeByName("v2");
        Node v3 = graph.getOrCreateNodeByName("v3");
        Node v4 = graph.getOrCreateNodeByName("v4");
        Node v5 = graph.getOrCreateNodeByName("v5");
        Node v6 = graph.getOrCreateNodeByName("v6");
        Node v7 = graph.getOrCreateNodeByName("v7");

        graph.addDependency(v1, v2, DependencyType.READ_ONLY);
        graph.addDependency(v2, v3, DependencyType.READ_ONLY);
        graph.addDependency(v3, v1, DependencyType.READ_ONLY);
        graph.addDependency(v1, v7, DependencyType.READ_ONLY);

        graph.addDependency(v4, v5, DependencyType.READ_ONLY);
        graph.addDependency(v5, v6, DependencyType.READ_ONLY);
        graph.addDependency(v6, v4, DependencyType.READ_ONLY);
        graph.addDependency(v4, v7, DependencyType.READ_ONLY);

        CycleFinder cf = new CycleFinderImpl(graph);
        assertTrue(cf.hasCycles());
        assertThat(cf.getCycles().size(), is(2)); // two cycles with 3 nodes each.
        assertThat(cf.getCycles().get(0).size(), is(3));
        assertThat(cf.getCycles().get(1).size(), is(3));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testOnlySimpleImpl() {
        DependencyGraph graph = createSomeGraph();
        new CycleFinderImpl(graph);
    }

    /**
     * just any graph, different from the standard implementation.
     *
     * @return a DependencyGraph.
     */
    private DependencyGraph createSomeGraph() {
        return new DependencyGraph() {

            @Override
            public Collection<Node> getAllNodes() {
                return null;
            }

            @Override
            public Collection<Dependency> getAllEdges() {
                return null;
            }

            @Override
            public Node getOrCreateNodeByName(String name) {
                return null;
            }

            @Override
            public Node getNode(String name) {
                return null;
            }

            @Override
            public boolean hasNode(String name) {
                return false;
            }

            @Override
            public Dependency addDependency(Node from, Node to, DependencyType type) {
                return null;
            }

            @Override
            public Dependency getEdge(Node from, Node to) {
                return null;
            }

            @Override
            public Set<Dependency> getOutgoingEdges(Node node) {
                return null;
            }

            @Override
            public Set<Dependency> getOutgoingEdges(Node node, DependencyType dependencyType) {
                return null;
            }

            @Override
            public Set<Dependency> getIncomingEdges(Node node) {
                return null;
            }

            @Override
            public Set<Dependency> getIncomingEdges(Node node, DependencyType dependencyType) {
                return null;
            }

            @Override
            public DependencyGraph filter(NodeFilter filter) {
                return null;
            }

            @Override
            public DependencyGraph filter(EdgeFilter filter) {
                return null;
            }

            @Override
            public DependencyGraph getBaseGraph() {
                return null;
            }
        };
    }
}
