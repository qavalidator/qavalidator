package de.qaware.qav.analysis.plugins.input

import de.qaware.qav.analysis.dsl.model.Analysis
import de.qaware.qav.analysis.plugins.base.BasePlugin
import de.qaware.qav.doc.QavCommand
import de.qaware.qav.doc.QavPluginDoc
import de.qaware.qav.graph.api.DependencyGraph
import de.qaware.qav.graph.factory.DependencyGraphFactory
import de.qaware.qav.input.maven.MavenInputReader

/**
 * Provides the Maven input for QAvalidator.
 *
 * @author Fabian Huch fabian.huch@qaware.de
 */
@QavPluginDoc(name = "MavenQavPlugin",
        description = "Provides the Maven input for QAvalidator")
class MavenQavPlugin extends BasePlugin {

    @Override
    void apply(Analysis analysis) {
        super.apply(analysis)
        analysis.register("inputMaven", this.&inputMaven)
    }

    /**
     * Reads all pom.xml files and creates mavenDependencyGraph and maven Architecture.
     *
     * @param rootDirName The path to the root pom file
     */
    @QavCommand(name = "inputMaven",
            description = """
                        Reads all pom.xml files and creates `mavenDependencyGraph` and `maven` Architecture.
                        """,
            parameters = [
                    @QavCommand.Param(name = "rootDirName", description = "The path to the root pom file"),
            ]
    )
    void inputMaven(String rootDirName) {
        MavenInputReader reader = new MavenInputReader(getGraph())
        reader.readPom(rootDirName)
    }

    /**
     * Lazy initialization for the Maven dependency graph
     *
     * @return the {@link DependencyGraph}
     */
    private DependencyGraph getGraph() {
        if (!context.mavenDependencyGraph) {
            context.mavenDependencyGraph = DependencyGraphFactory.createGraph()
        }
        return context.mavenDependencyGraph
    }
}
