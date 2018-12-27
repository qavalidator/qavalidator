package de.qaware.qav.app.server.mapper;

import de.qaware.qav.app.server.model.NodeDTO;
import de.qaware.qav.graph.api.Node;
import org.junit.Test;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link NodeMapper}. Also covers {@link DependencyMapper}.
 *
 * @author QAware GmbH
 */
public class NodeMapperTest extends AbstractMapperTest {

    @Test
    public void toDTO() {
        Node node = dependencyGraph.getNode("v1");
        NodeDTO nodeDTO = NodeMapper.toDTO(node, dependencyGraph);

        assertThat(nodeDTO.getName(), is("v1"));
        assertThat(nodeDTO.getOutgoingDeps(), hasSize(2));
        assertThat(nodeDTO.getOutgoingDeps().get(0).getSourceName(), is("v1"));
        assertThat(nodeDTO.getOutgoingDeps().get(0).getTargetName(), is("v2"));
        assertThat(nodeDTO.getOutgoingDeps().get(0).getTypeName(), is("READ_ONLY"));

        assertThat(nodeDTO.getParents(), hasSize(1));
        assertThat(nodeDTO.getParents().get(0).getSourceName(), is("p1"));
    }

    @Test
    public void toDTOParent() {
        Node p1 = dependencyGraph.getNode("p1");
        NodeDTO nodeDTO = NodeMapper.toDTO(p1, dependencyGraph);

        assertThat(nodeDTO.getName(), is("p1"));
        assertThat(nodeDTO.getContainedDeps(), hasSize(1));
        assertThat(nodeDTO.getContainedDeps().get(0).getSourceName(), is("p1"));
        assertThat(nodeDTO.getContainedDeps().get(0).getTargetName(), is("v1"));
        assertThat(nodeDTO.getContainedDeps().get(0).getTypeName(), is("CONTAINS"));

        assertThat(nodeDTO.getParents(), hasSize(0));
    }

    @Test(expected = NullPointerException.class)
    public void testNullInput() {
        NodeMapper.toDTO(null, dependencyGraph);
    }

    @Test(expected = NullPointerException.class)
    public void testNullInput2() {
        Node node = dependencyGraph.getNode("v1");
        NodeMapper.toDTO(node, null);
    }

}