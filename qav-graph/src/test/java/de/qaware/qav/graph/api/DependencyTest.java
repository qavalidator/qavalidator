package de.qaware.qav.graph.api;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
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
    public void testToString() {
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

    @Test
    public void testAddBaseDependency() {
        Node n1 = new Node("n1");
        Node n2 = new Node("n2");

        Dependency dependency = new Dependency(n1, n2, DependencyType.CREATE);
        assertThat(dependency.getBaseDependencies().isEmpty(), is(true));

        Node n3 = new Node("n3");
        Node n4 = new Node("n4");
        Dependency dep2 = new Dependency(n3, n4, DependencyType.READ_ONLY);

        dependency.addBaseDependency(dep2);
        assertThat(dependency.getBaseDependencies(), hasSize(1));

        Node n5 = new Node("n5");
        Node n6 = new Node("n6");
        Dependency dep3 = new Dependency(n5, n6, DependencyType.READ_WRITE);
        dependency.addBaseDependency(dep3);
        assertThat(dependency.getBaseDependencies(), hasSize(2));

        dependency.addBaseDependency(dep2);
        assertThat(dependency.getBaseDependencies(), hasSize(2)); // only added once.

        // another edge between the same nodes is added:
        Dependency dep2b = new Dependency(n3, n4, DependencyType.READ_WRITE);
        dependency.addBaseDependency(dep2b);
        assertThat(dependency.getBaseDependencies(), hasSize(3));
    }
}