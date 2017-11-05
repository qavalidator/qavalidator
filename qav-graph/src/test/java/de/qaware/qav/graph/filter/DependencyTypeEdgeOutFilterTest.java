package de.qaware.qav.graph.filter;

import de.qaware.qav.graph.api.Dependency;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.EdgeFilter;
import de.qaware.qav.graph.api.Node;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Tests for the {@link DependencyTypeEdgeOutFilter}
 *
 * @author QAware GmbH
 */
public class DependencyTypeEdgeOutFilterTest {

    @Test
    public void testIsAccepted() throws Exception {
        EdgeFilter filter1 = new DependencyTypeEdgeOutFilter(DependencyType.CONTAINS);

        Dependency dep1 = new Dependency(new Node("n1"), new Node("n2"), DependencyType.CONTAINS);
        assertThat(filter1.isAccepted(dep1), is(false));

        Dependency dep2 = new Dependency(new Node("n1"), new Node("n2"), DependencyType.INHERIT);
        assertThat(filter1.isAccepted(dep2), is(true));

        EdgeFilter filter2 = new DependencyTypeEdgeOutFilter(DependencyType.CONTAINS, DependencyType.INHERIT);
        assertThat(filter2.isAccepted(dep2), is(false));
    }
}