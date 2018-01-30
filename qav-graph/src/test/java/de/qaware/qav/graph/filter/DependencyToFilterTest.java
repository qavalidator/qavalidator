package de.qaware.qav.graph.filter;

import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.NodeFilter;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link DependencyToFilter}.
 *
 * @author QAware GmbH
 */
public class DependencyToFilterTest extends AbstractDependencyFilterTest {

    @Test
    public void testDependencyToFilter() {
        NodeFilter baseFilter = new NodeNameInFilter("java.util.Date", "org.hibernate.**");
        DependencyToFilter filter = new DependencyToFilter(baseFilter);

        DependencyGraph graph = dependencyGraph.filter(filter);

        assertThat(graph.getAllNodes(), hasSize(dependencyGraph.getAllNodes().size()));

        assertThat(graph.getEdge(a, b), nullValue());
        assertThat(graph.getEdge(a, c), nullValue());
        assertThat(graph.getEdge(a, d), nullValue());

        assertThat(graph.getEdge(a, e), notNullValue());
        assertThat(graph.getEdge(b, f), nullValue());
        assertThat(graph.getEdge(c, g), nullValue());

        assertThat(graph.getEdge(a, h), notNullValue());
        assertThat(graph.getEdge(a, i), notNullValue());
        assertThat(graph.getEdge(c, k), notNullValue());
        assertThat(graph.getEdge(c, m), notNullValue());
    }

}