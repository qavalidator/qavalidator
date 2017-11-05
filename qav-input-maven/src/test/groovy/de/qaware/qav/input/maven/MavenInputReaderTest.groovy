package de.qaware.qav.input.maven

import de.qaware.qav.architecture.dsl.model.Architecture
import de.qaware.qav.graph.api.DependencyGraph
import de.qaware.qav.graph.api.DependencyType
import de.qaware.qav.graph.api.Node
import de.qaware.qav.graph.factory.DependencyGraphFactory
import de.qaware.qav.visualization.GraphExporter
import org.junit.Test

/**
 * Tests for {@link MavenInputReader}.
 *
 * @author QAware GmbH
 */
class MavenInputReaderTest {

    private static final String ROOT_DIR = "src/test/resources/maven/pki"
    private static final String OUTPUT_DIR = "build/maven-test"

    @Test
    public void testReadPom() {
        DependencyGraph dependencyGraph = DependencyGraphFactory.createGraph()

        MavenInputReader mavenInputReader = new MavenInputReader(dependencyGraph)
        mavenInputReader.readPom(ROOT_DIR)

        assert dependencyGraph.getAllNodes().size() == 9
        assert dependencyGraph.getNode("com.mycompany.pki:pki") != null
        assert dependencyGraph.getNode("com.mycompany.pki:pki-api") != null
        assert dependencyGraph.getNode("com.mycompany.common:common-util") != null
        assert dependencyGraph.getNode("com.mycompany:mycompany-parent") != null

        assert dependencyGraph.getNode("joda-time") == null

        assert dependencyGraph.getAllEdges().size() == 12

        Node pki = dependencyGraph.getNode("com.mycompany.pki:pki")
        Node pki_api = dependencyGraph.getNode("com.mycompany.pki:pki-api")
        Node common_util = dependencyGraph.getNode("com.mycompany.common:common-util")
        Node parent = dependencyGraph.getNode("com.mycompany:mycompany-parent")

        assert dependencyGraph.getEdge(pki, parent) != null
        assert dependencyGraph.getEdge(pki, parent).getDependencyType() == DependencyType.INHERIT

        assert dependencyGraph.getEdge(pki, pki_api) != null
        assert dependencyGraph.getEdge(pki, pki_api).getDependencyType() == DependencyType.CONTAINS

        assert dependencyGraph.getEdge(pki_api, common_util) != null
        assert dependencyGraph.getEdge(pki_api, common_util).getDependencyType() == DependencyType.COMPILE

        GraphExporter.export(dependencyGraph, OUTPUT_DIR + "/mavenTest", new Architecture(), [], false)
    }


}
