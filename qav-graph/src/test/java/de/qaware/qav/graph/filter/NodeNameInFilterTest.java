package de.qaware.qav.graph.filter;

import de.qaware.qav.graph.api.Node;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests for the {@link NodeNameInFilter}
 *
 * @author QAware GmbH
 */
public class NodeNameInFilterTest {

    @Test
    public void testFilterPlain() {
        NodeNameInFilter filter = new NodeNameInFilter("x*");

        assertThat(filter.isAccepted(new Node("x1")), is(true));
        assertThat(filter.isAccepted(new Node("x219")), is(true));
        assertThat(filter.isAccepted(new Node("foo")), is(false));
    }

    @Test
    public void testFilterWithPackageNames() {
        NodeNameInFilter filter = new NodeNameInFilter("foo.*");

        assertThat(filter.isAccepted(new Node("foo.bar")), is(true));
        assertThat(filter.isAccepted(new Node("other.foo.bar")), is(false));
    }

    @Test
    public void testFilterMultipleNames() {
        NodeNameInFilter filter = new NodeNameInFilter("a.*", "b.*", "c.*");

        assertThat(filter.isAccepted(new Node("a.Clazz")), is(true));
        assertThat(filter.isAccepted(new Node("b.Clazz")), is(true));
        assertThat(filter.isAccepted(new Node("c.Clazz")), is(true));

        assertThat(filter.isAccepted(new Node("a.my.Clazz")), is(true));
        assertThat(filter.isAccepted(new Node("other.Clazz")), is(false));
    }

    @Test(expected = NullPointerException.class)
    public void testNullInput() {
        assertThat(new NodeNameInFilter("x*").isAccepted(null), is(false));
    }

}