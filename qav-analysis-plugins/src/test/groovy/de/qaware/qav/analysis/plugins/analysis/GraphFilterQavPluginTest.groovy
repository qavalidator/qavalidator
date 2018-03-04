package de.qaware.qav.analysis.plugins.analysis

import de.qaware.qav.analysis.plugins.test.TestAnalysis
import de.qaware.qav.graph.api.NodeFilter
import de.qaware.qav.graph.filter.NotFilter
import org.junit.Before
import org.junit.Test

/**
 * Tests for {@link GraphFilterQavPlugin}
 *
 * @author QAware GmbH
 */
class GraphFilterQavPluginTest {

    GraphFilterQavPlugin graphFilterQavPlugin = new GraphFilterQavPlugin()
    TestAnalysis analysis

    @Before
    void setup() {
        analysis = new TestAnalysis()
        graphFilterQavPlugin.apply(this.analysis)
    }

    @Test
    void testApply() {
        assert analysis.closureMap.size() == 16
    }

    @Test
    void testFilter() {
        NodeFilter filter = new NotFilter()
        assert analysis.errorMessages.size() == 0

        assert graphFilterQavPlugin.getFilter("myFilter") == null
        assert analysis.errorMessages.size() == 1

        graphFilterQavPlugin.addFilter("myFilter", filter)
        assert graphFilterQavPlugin.getFilter("myFilter") == filter
    }

}