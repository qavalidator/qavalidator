package de.qaware.qav.analysis.plugins.input

import de.qaware.qav.analysis.plugins.test.TestAnalysis
import de.qaware.qav.graph.api.DependencyGraph
import de.qaware.qav.graph.factory.DependencyGraphFactory
import de.qaware.qav.input.javacode.api.JavaScopeInput
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito

/**
 * Tests for {@link JavaQavPlugin}.
 *
 * @author QAware GmbH
 */
class JavaQavPluginTest {

    private JavaQavPlugin javaQavPlugin
    private JavaScopeInput javaScopeInputMock
    private TestAnalysis testAnalysis
    private DependencyGraph graph

    @Before
    void setup() {
        this.javaScopeInputMock = Mockito.mock(JavaScopeInput)
        this.javaQavPlugin = new JavaQavPlugin(javaScopeInputMock)
        testAnalysis = new TestAnalysis()
        graph = DependencyGraphFactory.createGraph()
        testAnalysis.context.dependencyGraph = graph
        this.javaQavPlugin.apply(testAnalysis)
    }

    @Test
    void apply() {
        assert testAnalysis.closureMap.size() == 1
    }

    @Test
    void inputJava() {
        javaQavPlugin.inputJava("dir-a", "dir-b")

        ArgumentCaptor<DependencyGraph> graphCaptor = ArgumentCaptor.forClass(DependencyGraph.class)
        ArgumentCaptor<Boolean> boolCaptor = ArgumentCaptor.forClass(Boolean.class)
        ArgumentCaptor<Map> mapCaptor = ArgumentCaptor.forClass(Map.class)

        Mockito.verify(javaScopeInputMock, Mockito.times(2))
                .read(graphCaptor.capture(), boolCaptor.capture(), mapCaptor.capture())

        assert graphCaptor.allValues[0] == graph
        assert graphCaptor.allValues[1] == graph

        assert boolCaptor.allValues[0]
        assert boolCaptor.allValues[1]

        assert mapCaptor.allValues[0]["baseDir"] == "dir-a"
        assert mapCaptor.allValues[0]["includes"] == ["**/*.class", "**/*.jar"]
        assert mapCaptor.allValues[1]["baseDir"] == "dir-b"
        assert mapCaptor.allValues[1]["includes"] == ["**/*.class", "**/*.jar"]
    }

    @Test
    void inputJava1() {
        javaQavPlugin.inputJava(false, "dir-a", "dir-b")

        ArgumentCaptor<DependencyGraph> graphCaptor = ArgumentCaptor.forClass(DependencyGraph.class)
        ArgumentCaptor<Boolean> boolCaptor = ArgumentCaptor.forClass(Boolean.class)
        ArgumentCaptor<Map> mapCaptor = ArgumentCaptor.forClass(Map.class)

        Mockito.verify(javaScopeInputMock, Mockito.times(2))
                .read(graphCaptor.capture(), boolCaptor.capture(), mapCaptor.capture())

        assert graphCaptor.allValues[0] == graph
        assert graphCaptor.allValues[1] == graph

        assert !boolCaptor.allValues[0]
        assert !boolCaptor.allValues[1]

        assert mapCaptor.allValues[0]["baseDir"] == "dir-a"
        assert mapCaptor.allValues[0]["includes"] == ["**/*.class", "**/*.jar"]
        assert mapCaptor.allValues[1]["baseDir"] == "dir-b"
        assert mapCaptor.allValues[1]["includes"] == ["**/*.class", "**/*.jar"]
    }

    @Test
    void inputJava2() {
        Map parameters = [
                "includes": ["**/*.class"],
                "baseDir" : "target/classes"
        ]

        javaQavPlugin.inputJava(parameters)

        ArgumentCaptor<DependencyGraph> graphCaptor = ArgumentCaptor.forClass(DependencyGraph.class)
        ArgumentCaptor<Boolean> boolCaptor = ArgumentCaptor.forClass(Boolean.class)
        ArgumentCaptor<Map> mapCaptor = ArgumentCaptor.forClass(Map.class)

        Mockito.verify(javaScopeInputMock, Mockito.times(1))
                .read(graphCaptor.capture(), boolCaptor.capture(), mapCaptor.capture())

        assert graphCaptor.allValues[0] == graph

        assert boolCaptor.allValues[0]

        assert mapCaptor.allValues[0]["baseDir"] == "target/classes"
        assert mapCaptor.allValues[0]["includes"] == ["**/*.class"]
    }

    @Test
    void inputJava3() {
        Map parameters = [
                "includes": ["**/*.class"],
                "baseDir" : "target/classes"
        ]

        javaQavPlugin.inputJava(parameters, false)

        ArgumentCaptor<DependencyGraph> graphCaptor = ArgumentCaptor.forClass(DependencyGraph.class)
        ArgumentCaptor<Boolean> boolCaptor = ArgumentCaptor.forClass(Boolean.class)
        ArgumentCaptor<Map> mapCaptor = ArgumentCaptor.forClass(Map.class)

        Mockito.verify(javaScopeInputMock, Mockito.times(1))
                .read(graphCaptor.capture(), boolCaptor.capture(), mapCaptor.capture())

        assert graphCaptor.allValues[0] == graph

        assert !boolCaptor.allValues[0]

        assert mapCaptor.allValues[0]["baseDir"] == "target/classes"
        assert mapCaptor.allValues[0]["includes"] == ["**/*.class"]
    }
}