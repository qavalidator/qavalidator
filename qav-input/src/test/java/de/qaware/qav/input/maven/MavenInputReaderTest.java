package de.qaware.qav.input.maven;

import de.qaware.qav.architecture.dsl.model.Architecture;
import de.qaware.qav.graph.api.Dependency;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.Node;
import de.qaware.qav.graph.factory.DependencyGraphFactory;
import de.qaware.qav.visualization.GraphExporter;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * Tests for {@link MavenInputReader}
 */
public class MavenInputReaderTest {

    private static final String ROOT_DIR = "src/test/resources/maven/pki";
    private static final String ROOT_DIR_SIMPLE = "src/test/resources/maven/simple";
    private static final String ROOT_WRONG_INPUT = "src/test/resources/maven/wrong";
    private static final String ROOT_NOT_EXISTING_INPUT = "src/test/resources/maven";
    private static final String OUTPUT_DIR = "build/maven-test";

    private DependencyGraph dependencyGraph;
    private MavenInputReader reader;

    @Before
    public void setup() {
        dependencyGraph = DependencyGraphFactory.createGraph();
        reader = new MavenInputReader(dependencyGraph);
    }

    @Test
    public void testRead() {
        reader.readPom(ROOT_DIR);

        assertThat(dependencyGraph.getAllNodes()).hasSize(10);

        Node pki = assertNode("com.mycompany.pki:pki");
        Node pkiApi = assertNode("com.mycompany.pki:pki-api");
        Node pkiServer = assertNode("com.mycompany.pki:pki-server");
        Node commonUtil = assertNode("com.mycompany.common:common-util");
        Node parent = assertNode("com.mycompany:mycompany-parent");
        Node tester = assertNode("org.testtools:tester");

        assertNode("joda-time:joda-time");
        assertThat(dependencyGraph.getNode("joda-time")).isNull();

        assertThat(dependencyGraph.getAllEdges()).hasSize(13);

        assertEdge(pki, parent, DependencyType.INHERIT);
        assertEdge(pki, pkiApi, DependencyType.CONTAINS);
        assertEdge(pkiApi, commonUtil, DependencyType.COMPILE);
        assertEdge(pkiServer, tester, DependencyType.TEST);

        GraphExporter.export(dependencyGraph, OUTPUT_DIR + "/mavenTest", new Architecture(), new ArrayList<>(), false);
    }

    @Test
    public void testSimple() {
        reader.readPom(ROOT_DIR_SIMPLE);

        assertThat(dependencyGraph.getAllNodes()).hasSize(5);
        Node abc = assertNode("com.mycompany.abc:abc");
        Node abcApi = assertNode("com.mycompany.abc:abc-api");
        Node common = assertNode("com.mycompany.common:common-util");
        Node guava = assertNode("com.google.guava:guava");
        Node tester = assertNode("org.testtools:tester");

        assertThat(dependencyGraph.getAllEdges()).hasSize(4);

        assertEdge(abc, abcApi, DependencyType.COMPILE);
        assertEdge(abc, common, DependencyType.COMPILE);
        assertEdge(abc, guava, DependencyType.COMPILE);
        assertEdge(abc, tester, DependencyType.TEST);
    }

    private Node assertNode(String name) {
        Node result = dependencyGraph.getNode(name);
        assertThat(result).isNotNull();
        return result;
    }

    private void assertEdge(Node from, Node to, DependencyType type) {
        Dependency edge = dependencyGraph.getEdge(from, to);
        assertThat(edge).isNotNull();
        assertThat(edge.getDependencyType()).isEqualTo(type);
    }

    @Test
    public void testWrongInput() {
        try {
            reader.readPom(ROOT_WRONG_INPUT);
            fail("IllegalArgumentException expected");
        } catch(IllegalArgumentException e) {
            assertThat(e).hasMessageStartingWith("com.fasterxml.jackson.core.JsonParseException: Unexpected character 'T'");
        }
    }

    @Test
    public void testNotExistingInput() {
        try {
            reader.readPom(ROOT_NOT_EXISTING_INPUT);
            fail("IllegalArgumentException expected");
        } catch(IllegalArgumentException e) {
            assertThat(e).hasMessageStartingWith("java.io.FileNotFoundException:");
        }
    }
}