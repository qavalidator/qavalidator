package de.qaware.qav.app.server.mapper;

import de.qaware.qav.app.server.model.DependencyDTO;
import de.qaware.qav.graph.api.Dependency;
import de.qaware.qav.graph.api.Node;
import org.junit.Test;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link DependencyMapper}.
 *
 * @author QAware GmbH
 */
public class DependencyMapperTest extends AbstractMapperTest {

    @Test
    public void testToDTO() {
        Node v1 = dependencyGraph.getNode("v1");
        Node v3 = dependencyGraph.getNode("v3");
        Dependency edge = dependencyGraph.getEdge(v1, v3);

        assertThat(v1, notNullValue());
        assertThat(v3, notNullValue());
        assertThat(edge, notNullValue());

        DependencyDTO dto = DependencyMapper.toDTO(edge);
        assertThat(dto, notNullValue());
        assertThat(dto.getSourceName(), is("v1"));
        assertThat(dto.getTargetName(), is("v3"));
        assertThat(dto.getTypeName(), is("CREATE"));
        assertThat(dto.getBaseDependencies(), hasSize(1));
        DependencyDTO baseDep = dto.getBaseDependencies().get(0);
        assertThat(baseDep.getSourceName(), is("v4"));
        assertThat(baseDep.getTargetName(), is("v5"));
        assertThat(baseDep.getTypeName(), is("READ_WRITE"));
    }

    @Test(expected = NullPointerException.class)
    public void testNullInput() {
        DependencyMapper.toDTO(null);
    }

}