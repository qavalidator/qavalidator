package de.qaware.qav.analysis.plugins.input

import de.qaware.qav.analysis.dsl.model.Analysis
import de.qaware.qav.analysis.plugins.base.BasePlugin
import de.qaware.qav.doc.QavCommand
import de.qaware.qav.doc.QavPluginDoc
import de.qaware.qav.graph.api.DependencyGraph
import de.qaware.qav.graph.factory.DependencyGraphFactory
import de.qaware.qav.input.traces.TraceReader

/**
 * Provides the Zipkin Traces input for QAvalidator.
 *
 * @author QAware GmbH
 */
@QavPluginDoc(name = "TracesQavPlugin",
        description = "Provides the Traces input for QAvalidator.")
class TracesQavPlugin extends BasePlugin {

    @Override
    void apply(Analysis analysis) {
        super.apply(analysis)
        analysis.register("inputTraces", this.&inputTraces)
    }

    /**
     * Reads Zipkin Traces JSON export file and creates the tracesDependencyGraph.
     *
     * @param filename The path to the Zipkin Traces JSON export file
     */
    @QavCommand(name = "inputTraces",
            description = """
                        Reads Zipkin Traces JSON export file and creates the tracesDependencyGraph.
                        """,
            parameters = [
                    @QavCommand.Param(name = "filename", description = "The path to the Zipkin Traces JSON export file "),
            ]
    )
    void inputTraces(String filename) {
        TraceReader reader = new TraceReader(getGraph())
        reader.read(filename)
    }

    /**
     * Lazy initialization for the Traces dependency graph
     *
     * @return the {@link de.qaware.qav.graph.api.DependencyGraph}
     */
    private DependencyGraph getGraph() {
        if (!context.tracesDependencyGraph) {
            context.tracesDependencyGraph = DependencyGraphFactory.createGraph()
        }
        return context.tracesDependencyGraph
    }
}
