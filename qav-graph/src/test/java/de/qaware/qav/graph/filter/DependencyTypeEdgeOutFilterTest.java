package de.qaware.qav.graph.filter;

import de.qaware.qav.graph.api.Dependency;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.EdgeFilter;
import de.qaware.qav.graph.api.Node;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests for the {@link DependencyTypeEdgeOutFilter}
 *
 * @author QAware GmbH
 */
public class DependencyTypeEdgeOutFilterTest {

    @Test
    public void testIsAccepted() {
        Dependency dep1 = new Dependency(new Node("n1"), new Node("n2"), DependencyType.CONTAINS);
        Dependency dep2 = new Dependency(new Node("n1"), new Node("n2"), DependencyType.INHERIT);
        Dependency dep3 = new Dependency(new Node("n1"), new Node("n2"), DependencyType.READ_ONLY);

        EdgeFilter filter1 = new DependencyTypeEdgeOutFilter(DependencyType.CONTAINS);
        assertThat(filter1.isAccepted(dep1), is(false));
        assertThat(filter1.isAccepted(dep2), is(true));

        EdgeFilter filter2 = new DependencyTypeEdgeOutFilter(DependencyType.CONTAINS, DependencyType.INHERIT);
        assertThat(filter2.isAccepted(dep1), is(false));
        assertThat(filter2.isAccepted(dep2), is(false));
        assertThat(filter2.isAccepted(dep3), is(true));

        EdgeFilter filter3 = new DependencyTypeEdgeOutFilter(DependencyType.CONTAINS, DependencyType.INHERIT, DependencyType.READ_ONLY);
        assertThat(filter3.isAccepted(dep3), is(false));
    }
}