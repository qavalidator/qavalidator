package de.qaware.qav.analysis.plugins.input

import de.qaware.qav.analysis.dsl.model.Analysis
import de.qaware.qav.analysis.plugins.base.BasePlugin
import de.qaware.qav.architecture.viewcreator.DependencyMapper
import de.qaware.qav.doc.QavCommand
import de.qaware.qav.doc.QavPluginDoc
import de.qaware.qav.graph.api.DependencyGraph
import de.qaware.qav.graph.factory.DependencyGraphFactory
import de.qaware.qav.input.typescript.TypescriptInputReader

/**
 * Provides the Typescript input for QAvalidator.
 *
 * @author QAware GmbH
 */
@QavPluginDoc(name = "TypescriptQavPlugin",
        description = "Provides the Typescript input for QAvalidator.")
class TypescriptQavPlugin extends BasePlugin {

    @Override
    void apply(Analysis analysis) {
        super.apply(analysis)
        analysis.register("inputTypescript", this.&inputTypescript)
    }

    /**
     * Reads Typescript XML export file and creates the typescriptDependencyGraph.
     *
     * @param filename The path to the Typescript XML export file
     */
    @QavCommand(name = "inputTypescript",
            description = """
                        Reads Typescript XML export file and creates the typescriptDependencyGraph.
                        """,
            parameters = [
                    @QavCommand.Param(name = "filename", description = "The path to the Typescript XML export file"),
            ]
    )
    void inputTypescript(String filename) {
        TypescriptInputReader reader = new TypescriptInputReader(getGraph())
        reader.read(filename)
        DependencyMapper.mapDependencies(getGraph(), "typescript")
    }

    /**
     * Lazy initialization for the Typescript dependency graph
     *
     * @return the {@link DependencyGraph}
     */
    private DependencyGraph getGraph() {
        if (!context.typescriptDependencyGraph) {
            context.typescriptDependencyGraph = DependencyGraphFactory.createGraph()
        }
        return context.typescriptDependencyGraph
    }
}
