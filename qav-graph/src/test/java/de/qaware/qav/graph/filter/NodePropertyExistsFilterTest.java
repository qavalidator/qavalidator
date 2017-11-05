package de.qaware.qav.graph.filter;

import de.qaware.qav.graph.api.Node;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link NodePropertyExistsFilter}
 *
 * @author QAware GmbH
 */
public class NodePropertyExistsFilterTest {

    @Test
    public void testIsAccepted() {
        Node node = new Node("x");
        node.setProperty("p1", true);
        node.setProperty("p2", null);

        NodePropertyExistsFilter p1 = new NodePropertyExistsFilter("p1");
        assertTrue(p1.isAccepted(node));
        NodePropertyExistsFilter p2 = new NodePropertyExistsFilter("p2");
        assertFalse(p2.isAccepted(node));
        NodePropertyExistsFilter p3 = new NodePropertyExistsFilter("p3");
        assertFalse(p3.isAccepted(node));
    }

    @Test
    public void testAddFilter() {
        Node node = new Node("x");
        node.setProperty("p1", true);

        NodePropertyExistsFilter p1 = new NodePropertyExistsFilter("p1");
        p1.addFilter("p2");
        assertFalse(p1.isAccepted(node));

        node.setProperty("p2", "Yes");
        assertTrue(p1.isAccepted(node));
    }

    @Test
    public void testNoFilter() {
        Node node = new Node("x");
        node.setProperty("p1", true);

        NodePropertyExistsFilter p1 = new NodePropertyExistsFilter(); // silly, but valid.

        assertTrue(p1.isAccepted(node));
    }
}