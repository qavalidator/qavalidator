package de.qaware.qav.graph.filter;

import de.qaware.qav.graph.api.Node;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link NodePropertyInFilter}.
 *
 * @author QAware GmbH
 */
public class NodePropertyInFilterTest {

    @Test
    public void testFiltered() {
        Node node = new Node("x");
        node.setProperty("p1", true);

        NodePropertyInFilter propertyInFilter = new NodePropertyInFilter();
        propertyInFilter.addFilter("p2", true);
        assertFalse(propertyInFilter.isAccepted(node));

        propertyInFilter = new NodePropertyInFilter("p1", true);
        assertTrue(propertyInFilter.isAccepted(node));

        propertyInFilter = new NodePropertyInFilter();
        propertyInFilter.addFilter("p1", false);
        assertFalse(propertyInFilter.isAccepted(node));
    }

    @Test
    public void testMultipleProperties() {
        Node node = new Node("x");
        node.setProperty("p1", true);
        node.setProperty("p2", "Hello");

        NodePropertyInFilter propertyInFilter = new NodePropertyInFilter();
        propertyInFilter.addFilter("p1", true);
        propertyInFilter.addFilter("p2", "Hello");
        assertTrue(propertyInFilter.isAccepted(node));

        propertyInFilter = new NodePropertyInFilter();
        propertyInFilter.addFilter("p1", true);
        propertyInFilter.addFilter("p2", "something different");
        assertFalse(propertyInFilter.isAccepted(node));
    }

    @Test
    public void testListProperties() {
        Node node = new Node("x");
        node.addListProperty("p1", "v1");
        node.addListProperty("p1", "v2");

        NodePropertyInFilter propertyInFilter = new NodePropertyInFilter();
        propertyInFilter.addFilter("p1", "v1");
        assertTrue(propertyInFilter.isAccepted(node));
    }
}