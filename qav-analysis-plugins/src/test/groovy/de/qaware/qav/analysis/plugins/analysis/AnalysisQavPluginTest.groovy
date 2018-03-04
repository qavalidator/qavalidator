package de.qaware.qav.analysis.plugins.analysis

import de.qaware.qav.analysis.plugins.test.TestAnalysis
import de.qaware.qav.architecture.dsl.api.QavArchitectureReader
import de.qaware.qav.architecture.dsl.model.Architecture
import de.qaware.qav.architecture.nodecreator.ArchitectureViewCreator
import de.qaware.qav.architecture.nodecreator.Result
import de.qaware.qav.graph.api.DependencyGraph
import de.qaware.qav.graph.api.DependencyType
import de.qaware.qav.graph.factory.DependencyGraphFactory
import org.junit.Before
import org.junit.Test

/**
 * Tests for {@link AnalysisQavPlugin}
 *
 * @author QAware GmbH
 */
class AnalysisQavPluginTest {

    AnalysisQavPlugin analysisQavPlugin = new AnalysisQavPlugin()
    TestAnalysis analysis

    @Before
    void setup() {
        analysis = new TestAnalysis()
        analysisQavPlugin.apply(this.analysis)
    }

    @Test
    void testApply() {
        assert analysis.closureMap.size() == 5
    }

    @Test
    void testFindCyclesNoCycles() {
        DependencyGraph graph = createSampleGraph(false)
        DependencyGraph cycles = analysisQavPlugin.findCycles(graph, "my scope")

        assert cycles.allNodes.isEmpty()
    }

    @Test
    void testFindCycles() {
        DependencyGraph graph = createSampleGraph(true)
        DependencyGraph cycles = analysisQavPlugin.findCycles(graph, "my scope")

        assert cycles.allNodes.size() == 3
    }

    @Test
    void testCheckArchitectureRules() {
        DependencyGraph graph = createGraphWithUnwantedRelations()
        Architecture architecture = readArchitecture()
        Result result = ArchitectureViewCreator.createArchitectureView(graph, architecture, null)

        assert result.violationMessage == null
        DependencyGraph architectureGraph = result.architectureGraph

        analysisQavPlugin.checkArchitectureRules(architectureGraph, architecture)

        assert analysis.calledMethods.size() == 2
        assert analysis.calledMethodsArgs["violation"] == [
                "Architecture Checker found violations: DependencyChecker: 1 VIOLATIONS in architecture view T-View: 1 uncovered dependencies: [V --[READ_ONLY]--> Unwanted]"
        ]
        assert analysis.calledMethodsArgs["sonarError"] == [
                "DependencyChecker: 1 VIOLATIONS in architecture view T-View: 1 uncovered dependencies: [V --[READ_ONLY]--> Unwanted]"
        ]
    }

    @Test
    void testCheckDependencyRules() {
        DependencyGraph graph = createGraphWithUnwantedRelations()
        Architecture architecture = readArchitecture()
        Result result = ArchitectureViewCreator.createArchitectureView(graph, architecture, null)

        assert result.violationMessage == null
        DependencyGraph architectureGraph = result.architectureGraph

        analysisQavPlugin.checkDependencyRules(architectureGraph, architecture)

        assert analysis.calledMethods.size() == 2
        assert analysis.calledMethodsArgs["violation"] == [
                "Architecture Checker found violations: DependencyChecker: 1 VIOLATIONS in architecture view T-View: 1 uncovered dependencies: [V --[READ_ONLY]--> Unwanted]"
        ]
        assert analysis.calledMethodsArgs["sonarError"] == [
                "DependencyChecker: 1 VIOLATIONS in architecture view T-View: 1 uncovered dependencies: [V --[READ_ONLY]--> Unwanted]"
        ]
    }

    @Test
    void testFindDependenciesTo() {
        DependencyGraph graph = createGraphWithUnwantedRelations()
        DependencyGraph unwanted = analysisQavPlugin.findDependenciesTo(graph, "unwanted.*")

        assert unwanted.allNodes.size() == 4
        assert unwanted.hasNode("v1")
        assert unwanted.hasNode("v2")
        assert !unwanted.hasNode("v3")
    }

    static private DependencyGraph createSampleGraph(boolean withCycle) {
        DependencyGraph graph = DependencyGraphFactory.createGraph()
        def v1 = graph.getOrCreateNodeByName("v1")
        def v2 = graph.getOrCreateNodeByName("v2")
        def v3 = graph.getOrCreateNodeByName("v3")
        graph.addDependency(v1, v2, DependencyType.READ_ONLY)
        graph.addDependency(v2, v3, DependencyType.READ_ONLY)

        if (withCycle) {
            graph.addDependency(v3, v1, DependencyType.READ_WRITE)
        }

        graph
    }


    // --- helpers

    static private DependencyGraph createGraphWithUnwantedRelations() {
        DependencyGraph graph = DependencyGraphFactory.createGraph()
        def v1 = graph.getOrCreateNodeByName("v1")
        def v2 = graph.getOrCreateNodeByName("v2")
        def v3 = graph.getOrCreateNodeByName("v3")
        def uw1 = graph.getOrCreateNodeByName("unwanted.x1")
        def uw2 = graph.getOrCreateNodeByName("unwanted.x2")

        graph.addDependency(v1, uw1, DependencyType.READ_ONLY)
        graph.addDependency(v2, uw2, DependencyType.READ_ONLY)
        graph.addDependency(v3, v1, DependencyType.READ_ONLY)

        graph
    }

    static private Architecture readArchitecture() {
        QavArchitectureReader reader = new QavArchitectureReader("src/test/resources/testArchitecture.groovy", null)
        reader.read()

        Architecture architecture = reader.getArchitectures()["T-View"]
        assert architecture != null
        architecture
    }
}