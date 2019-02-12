package de.qaware.qav.analysis.plugins.analysis

import de.qaware.qav.analysis.plugins.test.TestAnalysis
import de.qaware.qav.architecture.dsl.model.Architecture
import de.qaware.qav.graph.api.DependencyGraph
import de.qaware.qav.graph.factory.DependencyGraphFactory
import org.junit.Before
import org.junit.Test

/**
 * Tests for {@link ArchitectureQavPlugin}.
 *
 * @author QAware GmbH
 */
class ArchitectureQavPluginTest {

    ArchitectureQavPlugin architectureQavPlugin = new ArchitectureQavPlugin()
    TestAnalysis analysis

    @Before
    void setup() {
        analysis = new TestAnalysis()
        architectureQavPlugin.apply(this.analysis)
    }

    @Test
    void testApply() {
        assert analysis.closureMap.size() == 6
    }

    @Test
    void testGetArchitecture() {
        assert architectureQavPlugin.getArchitecture("my-arch") == null
        assert analysis.errorMessages.size() == 1
        assert analysis.errorMessages[0] == "Architecture my-arch not found."

        architectureQavPlugin.addArchitecture(new Architecture(name: "my-arch"))
        assert architectureQavPlugin.getArchitecture("my-arch").name == "my-arch"
    }

    @Test
    void testReadArchitecture() {
        assert architectureQavPlugin.getArchitecture("T-View") == null
        assert analysis.errorMessages[0] == "Architecture T-View not found."

        analysis = new TestAnalysis() // reset

        architectureQavPlugin.readArchitecture("classpath:/testArchitecture.groovy")
        assert architectureQavPlugin.getArchitecture("T-View").name == "T-View"
        assert !analysis.errorMessages
    }

    @Test
    void testCreatePackageArchitectureView() {
        DependencyGraph graph = DependencyGraphFactory.createGraph()
        graph.getOrCreateNodeByName("com.my.a.A1")
        graph.getOrCreateNodeByName("com.my.a.A2")
        graph.getOrCreateNodeByName("com.my.b.B1")
        graph.getOrCreateNodeByName("com.my.util.X")
        graph.getOrCreateNodeByName("org.other.Y")

        DependencyGraph view = architectureQavPlugin.createPackageArchitectureView(graph)

        assert view.allNodes.size() == 8
        assert view.hasNode("com.my.a")
        assert view.hasNode("com.my")
        assert view.hasNode("com")
        assert view.hasNode("com.my.b")
        assert view.hasNode("com.my.util")
        assert view.hasNode("org.other")
        assert view.hasNode("org")
        assert view.hasNode("Package")
    }

    @Test
    void testCreatePackageArchitectureViewMaxDepth() {
        DependencyGraph graph = DependencyGraphFactory.createGraph()
        graph.getOrCreateNodeByName("com.my.a.A1")
        graph.getOrCreateNodeByName("com.my.a.A2")
        graph.getOrCreateNodeByName("com.my.b.B1")
        graph.getOrCreateNodeByName("com.my.util.X")
        graph.getOrCreateNodeByName("org.other.Y")

        DependencyGraph view = architectureQavPlugin.createPackageArchitectureView(graph, 2)

        assert view.allNodes.size() == 5
        assert view.hasNode("com.my")
        assert view.hasNode("com")
        assert view.hasNode("org.other")
        assert view.hasNode("org")
        assert view.hasNode("Package-2")
    }

    @Test
    void testCreatePackageArchitectureViewOtherSeparator() {
        DependencyGraph graph = DependencyGraphFactory.createGraph()
        graph.getOrCreateNodeByName("com#my#a#A1")
        graph.getOrCreateNodeByName("com#my#a#A2")
        graph.getOrCreateNodeByName("com#my#b#B1")
        graph.getOrCreateNodeByName("com#my#util#X")
        graph.getOrCreateNodeByName("org#other#Y")

        DependencyGraph view = architectureQavPlugin.createPackageArchitectureView(graph, "Package-#","#", 2)
        assert view.allNodes.size() == 5
        assert view.hasNode("com#my")
        assert view.hasNode("com")
        assert view.hasNode("org#other")
        assert view.hasNode("org")
        assert view.hasNode("Package-#-2")
    }

    @Test
    void testCreateArchitectureViewWithViolation() {
        architectureQavPlugin.readArchitecture("classpath:/testArchitecture.groovy")
        Architecture architecture = architectureQavPlugin.getArchitecture("T-View")

        DependencyGraph graph = DependencyGraphFactory.createGraph()
        graph.getOrCreateNodeByName("v1")
        graph.getOrCreateNodeByName("v2")
        graph.getOrCreateNodeByName("unwanted.x1")
        graph.getOrCreateNodeByName("unwanted.x2")
        graph.getOrCreateNodeByName("org.other.Y")

        DependencyGraph view = architectureQavPlugin.createArchitectureView(graph, architecture)

        assert analysis.violationMessages == ["There are unmapped classes in architecture T-View: [org.other.Y]"]
        assert view.allNodes.size() == 4
        assert view.hasNode("V")
        assert view.hasNode("Unwanted")
        assert view.hasNode("3rdParty")
        assert view.hasNode("T-View")
    }
}
