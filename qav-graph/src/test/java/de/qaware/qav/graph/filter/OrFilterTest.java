package de.qaware.qav.graph.filter;

import de.qaware.qav.graph.api.Node;
import de.qaware.qav.graph.api.NodeFilter;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Tests for the OR filter.
 *
 * @author QAware GmbH
 */
public class OrFilterTest {

    @Test
    public void testIsAccepted() throws Exception {
        NodeFilter f1 = new OrFilter(new TrueFilter(), new TrueFilter());
        assertThat(f1.isAccepted(new Node("x")), is(true));

        NodeFilter f2 = new OrFilter(new TrueFilter(), new NotFilter(new TrueFilter()));
        assertThat(f2.isAccepted(new Node("x")), is(true));

        NodeFilter f3 = new OrFilter(new NotFilter(new TrueFilter()), new NotFilter(new TrueFilter()));
        assertThat(f3.isAccepted(new Node("x")), is(false));
    }
}