package de.qaware.qav.graph.filter;

import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.Node;
import de.qaware.qav.graph.factory.DependencyGraphFactory;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests for the {@link NodeHasIncomingDependencyTypeFilter}
 *
 * @author QAware GmbH
 */
public class NodeHasIncomingDependencyTypeFilterTest {

    @Test
    public void testIsAccepted() {
        DependencyGraph graph = DependencyGraphFactory.createGraph();

        Node n1 = graph.getOrCreateNodeByName("v1");
        Node n2 = graph.getOrCreateNodeByName("v2");
        Node n3 = graph.getOrCreateNodeByName("v3");
        graph.addDependency(n1, n2, DependencyType.READ_ONLY);
        graph.addDependency(n1, n3, DependencyType.CREATE);

        NodeHasIncomingDependencyTypeFilter filter = new NodeHasIncomingDependencyTypeFilter(graph, DependencyType.CREATE);
        assertThat(filter.isAccepted(n1), is(false));
        assertThat(filter.isAccepted(n2), is(false));
        assertThat(filter.isAccepted(n3), is(true));
    }
}