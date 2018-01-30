package de.qaware.qav.graph.filter;

import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.NodeFilter;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link NodeHasDependencyToFilter}.
 *
 * @author QAware GmbH
 */
public class NodeHasDependencyToFilterTest extends AbstractDependencyFilterTest {

    @Test
    public void testRelationToFilter() {
        NodeFilter baseFilter = new NodeNameInFilter("java.util.Date", "org.hibernate.**");
        NodeHasDependencyToFilter filter = new NodeHasDependencyToFilter(dependencyGraph, baseFilter);

        assertThat(filter.isAccepted(a), is(true));
        assertThat(filter.isAccepted(b), is(false));
        assertThat(filter.isAccepted(c), is(true));
        assertThat(filter.isAccepted(d), is(false));

        assertThat(filter.isAccepted(e), is(true));
        assertThat(filter.isAccepted(f), is(false));
        assertThat(filter.isAccepted(g), is(false));

        assertThat(filter.isAccepted(h), is(true));
        assertThat(filter.isAccepted(i), is(true));
        assertThat(filter.isAccepted(k), is(true));
        assertThat(filter.isAccepted(m), is(true));

        DependencyGraph graph = dependencyGraph.filter(filter);

        assertThat(graph.getEdge(a, b), nullValue());
        assertThat(graph.getEdge(a, c), notNullValue());
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