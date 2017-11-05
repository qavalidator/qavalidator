package de.qaware.qav.graph.filter;

import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.Node;
import de.qaware.qav.graph.factory.DependencyGraphFactory;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests for the {@link DependencyTypeNodeFilter}
 *
 * @author QAware GmbH
 */
public class DependencyTypeNodeFilterTest {

    @Test
    public void testIsAccepted() throws Exception {
        DependencyGraph graph = DependencyGraphFactory.createGraph();

        Node n1 = graph.getOrCreateNodeByName("v1");
        Node n2 = graph.getOrCreateNodeByName("v2");
        Node n3 = graph.getOrCreateNodeByName("v3");
        graph.addDependency(n1, n2, DependencyType.READ_ONLY);
        graph.addDependency(n1, n3, DependencyType.CREATE);

        DependencyTypeNodeFilter filter = new DependencyTypeNodeFilter(graph, DependencyType.CREATE);
        assertThat(filter.isAccepted(n1), is(true));
        assertThat(filter.isAccepted(n2), is(false));
        assertThat(filter.isAccepted(n3), is(true));
    }
}