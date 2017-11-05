package de.qaware.qav.graph.api;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link Dependency}.
 *
 * @author QAware GmbH
 */
public class DependencyTest {

    @Test
    public void testDependencyType() {
        Node n1 = new Node("n1");
        Node n2 = new Node("n2");

        Dependency dependency = new Dependency(n1, n2, DependencyType.CREATE);
        assertThat(dependency.getDependencyType(), is(DependencyType.CREATE));
        dependency.setDependencyType(DependencyType.READ_ONLY);
        assertThat(dependency.getDependencyType(), is(DependencyType.READ_ONLY));
    }

    @Test
    public void testToString() throws Exception {
        Node n1 = new Node("n1");
        Node n2 = new Node("n2");

        Dependency dependency = new Dependency(n1, n2, DependencyType.CREATE);

        assertThat(dependency.getDependencyType(), is(DependencyType.CREATE));
        assertThat(dependency.toString(), is("n1 --[CREATE]--> n2"));
    }

    @Test(expected = NullPointerException.class)
    public void testSourceNotNull() {
        Node n2 = new Node("n2");
        new Dependency(null, n2, DependencyType.CREATE);
    }

    @Test(expected = NullPointerException.class)
    public void testTargetNotNull() {
        Node n1 = new Node("n1");
        new Dependency(n1, null, DependencyType.CREATE);
    }

    @Test(expected = NullPointerException.class)
    public void testDependencyTypeNotNull() {
        Node n1 = new Node("n1");
        Node n2 = new Node("n2");
        new Dependency(n1, n2, null);
    }

    @Test(expected = NullPointerException.class)
    public void testDependencyTypeNeverNull() {
        Node n1 = new Node("n1");
        Node n2 = new Node("n2");

        Dependency dependency = new Dependency(n1, n2, DependencyType.CREATE);
        assertThat(dependency.getDependencyType(), is(DependencyType.CREATE));
        dependency.setDependencyType(null);
    }
}