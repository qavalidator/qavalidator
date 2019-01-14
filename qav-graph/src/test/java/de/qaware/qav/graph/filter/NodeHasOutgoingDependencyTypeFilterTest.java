package de.qaware.qav.graph.filter;

import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.Node;
import de.qaware.qav.graph.factory.DependencyGraphFactory;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 */
public class NodeHasOutgoingDependencyTypeFilterTest {

    @Test
    public void testIsAccepted() {
        DependencyGraph graph = DependencyGraphFactory.createGraph();

        Node n1 = graph.getOrCreateNodeByName("v1");
        Node n2 = graph.getOrCreateNodeByName("v2");
        Node n3 = graph.getOrCreateNodeByName("v3");
        graph.addDependency(n1, n2, DependencyType.READ_ONLY);
        graph.addDependency(n1, n3, DependencyType.CREATE);

        NodeHasOutgoingDependencyTypeFilter filter = new NodeHasOutgoingDependencyTypeFilter(graph, DependencyType.CREATE);
        assertThat(filter.isAccepted(n1)).isTrue();
        assertThat(filter.isAccepted(n2)).isFalse();
        assertThat(filter.isAccepted(n3)).isFalse();
    }
}