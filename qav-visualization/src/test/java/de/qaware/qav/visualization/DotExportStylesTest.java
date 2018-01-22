package de.qaware.qav.visualization;

import de.qaware.qav.graph.alg.api.CycleFinder;
import de.qaware.qav.graph.api.Dependency;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.Node;
import org.junit.Test;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link DotExportStyles}.
 *
 * @author QAware GmbH
 */
public class DotExportStylesTest {

    @Test
    public void testGetNodeStyle() {
        Node node = new Node("my-node");
        assertThat(DotExportStyles.getNodeStyle(node), is("color = black"));

        node.setProperty(CycleFinder.IN_CYCLE, true);
        assertThat(DotExportStyles.getNodeStyle(node), is("color = red, penwidth = 3.0"));
    }

    @Test
    public void testGetNodeUrl() {
        assertThat(DotExportStyles.getNodeUrl("com_my"), is("/#/node/com_my"));
    }

    @Test
    public void testGetEdgeStyle()  {
        Dependency dependency = mock(Dependency.class);
        when(dependency.getDependencyType()).thenReturn(DependencyType.CREATE);
        assertThat(DotExportStyles.getEdgeStyle(dependency), is("color = \"#b2182b\", fontcolor = \"#b2182b\", penwidth = 3.0, style = solid"));
    }

    @Test
    public void testGetRankSep() {
        assertThat(DotExportStyles.getRankSep(16), greaterThan(1.1));
        assertThat(DotExportStyles.getRankSep(16), lessThan(1.3));

        assertThat(DotExportStyles.getRankSep(250), greaterThan(4.7));
        assertThat(DotExportStyles.getRankSep(250), lessThan(4.8));
    }
}