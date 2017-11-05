package de.qaware.qav.server.mapper;

import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.Node;
import de.qaware.qav.graph.io.GraphReaderWriter;
import org.junit.Before;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Base for mapper tests.
 *
 * @author QAware GmbH
 */
public class AbstractMapperTest {

    private static final String TEST_GRAPH = "src/test/resources/testGraph1.json";

    protected DependencyGraph dependencyGraph;

    @Before
    public void setup() {
        dependencyGraph = GraphReaderWriter.read(TEST_GRAPH);
        assertThat(dependencyGraph, notNullValue());
        Node p1 = dependencyGraph.getOrCreateNodeByName("p1");
        Node n1 = dependencyGraph.getNode("v1");
        dependencyGraph.addDependency(p1, n1, DependencyType.CONTAINS);
    }

}
