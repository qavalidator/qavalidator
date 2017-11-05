package de.qaware.qav.graph.api;

import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link Node} and {@link AbstractGraphElement}.
 *
 * @author QAware GmbH
 */
public class NodeTest {

    @Test
    public void testNodeEquals() {
        Node n1 = new Node("n1");
        n1.setProperty("x1", true);
        Node n2 = new Node("n1");
        n2.setProperty("x1", false);

        assertThat(n1 == n2, is(false));
        assertThat(n1.equals(n2), is(true));
        assertThat(n1.equals(null), is(false));
        assertThat(n1.equals("some-other-data-type"), is(false));
    }

    @Test
    public void testNodeProperties() {
        Node n1 = new Node("n1");

        assertThat(n1.getProperty("x1"), nullValue());
        assertThat(n1.hasProperty("x1"), is(false));

        n1.setProperty("x1", true);
        assertThat((Boolean) n1.getProperty("x1"), is(true));
        assertThat(n1.hasProperty("x1"), is(true));

        n1.setProperty("x1", null);
        assertThat(n1.getProperty("x1"), nullValue());
        assertThat(n1.hasProperty("x1"), is(false));

        n1.setProperty("x1", true);
        assertThat((Boolean) n1.getProperty("x1"), is(true));
        assertThat(n1.hasProperty("x1"), is(true));
    }

    @Test
    public void testGetPropertyWithDefault() {
        Node n1 = new Node("n1");
        assertThat(n1.getProperty("key"), Matchers.nullValue());
        assertThat(n1.getProperty("key", "Hello Default"), Matchers.is("Hello Default"));
        n1.setProperty("key", "Hello");
        assertThat(n1.getProperty("key", "Hello Default"), Matchers.is("Hello"));
    }

    @Test
    public void testGetPropertyWithDefaultWithWrongType() {
        Node n1 = new Node("n1");
        assertThat(n1.getProperty("key"), Matchers.nullValue());
        assertThat(n1.getProperty("key", "Hello Default"), Matchers.is("Hello Default"));
        n1.setProperty("key", 12L);
        assertThat(n1.getProperty("key", "Hello Default"), Matchers.is(12L));
    }
    @Test(expected = IllegalArgumentException.class)
    public void testNodeNameIsFix() {
        Node n1 = new Node("n1");
        n1.setProperty("name", "n2"); // not allowed.
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNodeNameCantBeRemoved() {
        Node n1 = new Node("n1");
        n1.setProperty("name", null); // not allowed.
    }

    @Test(expected = NullPointerException.class)
    public void testNodeNameNeverNull() {
        new Node(null);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testListProperty() {
        Node n1 = new Node("n1");

        assertThat(n1.getProperty("p1"), nullValue());
        n1.addListProperty("p1", 1);
        assertThat(n1.getProperty("p1"), notNullValue());
        assertThat(n1.getProperty("p1") instanceof List, is(true));

        assertThat(n1.getProperty("p2"), nullValue());
        n1.setProperty("p2", 2);
        assertThat(n1.getProperty("p2") instanceof Integer, is(true));
        n1.addListProperty("p2", 3);
        assertThat(n1.getProperty("p2") instanceof List, is(true));
        n1.addListProperty("p2", 4);
        assertThat(((List<Object>) n1.getProperty("p2")).size(), is(3));
        assertThat(((List<Object>) n1.getProperty("p2")).containsAll(Arrays.asList(2, 3, 4)), is(true));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testListPropertyNoDuplicates() {
        Node n1 = new Node("n1");
        n1.addListProperty("p1", 1);
        n1.addListProperty("p1", 2);
        n1.addListProperty("p1", 3);
        n1.addListProperty("p1", 2);

        assertThat(((List<Object>) n1.getProperty("p1")).size(), is(3));
        assertThat(((List<Object>) n1.getProperty("p1")).containsAll(Arrays.asList(1, 2, 3)), is(true));
    }
}