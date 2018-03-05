package de.qaware.qav.architecture.viewcreator;

import de.qaware.qav.architecture.dsl.api.QavArchitectureReader;
import de.qaware.qav.architecture.dsl.model.Architecture;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.Node;
import de.qaware.qav.graph.factory.DependencyGraphFactory;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link ArchitectureViewCreator}.
 *
 * @author QAware GmbH
 */
public class ArchitectureViewCreatorTest {

    private DependencyGraph graph;
    private Architecture architecture;

    @Before
    public void init() {
        initGraph();
        initArchitecture();
    }

    private void initGraph() {
        graph = DependencyGraphFactory.createGraph();

        Node a1 = graph.getOrCreateNodeByName("com.my.a.api.a1");
        Node a2 = graph.getOrCreateNodeByName("com.my.a.impl.a2");
        graph.getOrCreateNodeByName("com.my.a.d.api.a3");
        graph.getOrCreateNodeByName("com.my.a.d.impl.a3");

        graph.getOrCreateNodeByName("com.my.bb.api.b1");
        graph.getOrCreateNodeByName("com.my.bb.impl.b2");

        graph.getOrCreateNodeByName("com.my.cc.api.xx.c1");
        graph.getOrCreateNodeByName("com.my.cc.impl.xx.c2");

        Node g = graph.getOrCreateNodeByName("org.other.g");

        graph.addDependency(a1, a2, DependencyType.READ_WRITE);
        graph.addDependency(a1, g, DependencyType.READ_WRITE);
    }

    private void initArchitecture() {
        QavArchitectureReader reader = new QavArchitectureReader("src/test/resources/qa/arch-for-view-creator-test.groovy", null);
        reader.read();
        this.architecture = reader.getArchitectures().get("Test-Architecture");
    }

    @Test
    public void testCreateArchitectureView() {
        Result result = ArchitectureViewCreator.createArchitectureView(graph, architecture, null);

        assertThat(result.getViolationMessage(), nullValue());

        assertResultingArchitecureGraph(result.getArchitectureGraph());
    }

    @Test
    public void testCreateArchitectureViewWithUnmappedClasses() {
        graph.getOrCreateNodeByName("com.my.other.nodes.A");

        Result result = ArchitectureViewCreator.createArchitectureView(graph, architecture, architecture.getName());

        assertThat(result.getViolationMessage(), is("There are unmapped classes in architecture Test-Architecture: [com.my.other.nodes.A]"));

        // rest remains unchanged, compare above:
        assertResultingArchitecureGraph(result.getArchitectureGraph());
    }

    private void assertResultingArchitecureGraph(DependencyGraph architectureGraph) {
        assertThat(architectureGraph.getAllNodes(), hasSize(6));
        assertThat(architectureGraph.getAllEdges(), hasSize(6));

        Node a = architectureGraph.getNode("A");
        Node thirdParty = architectureGraph.getNode("3rd-Party");
        assertThat(a, notNullValue());
        assertThat(thirdParty, notNullValue());
        assertThat(architectureGraph.getEdge(a, thirdParty), notNullValue());
    }
}
