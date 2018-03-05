package de.qaware.qav.analysis.plugins.analysis

import de.qaware.qav.analysis.dsl.model.Analysis
import de.qaware.qav.analysis.plugins.base.BasePlugin
import de.qaware.qav.architecture.dsl.api.QavArchitectureReader
import de.qaware.qav.architecture.dsl.model.Architecture
import de.qaware.qav.architecture.factory.DefaultPackageArchitectureFactory
import de.qaware.qav.architecture.tagger.ArchitectureHeightTagger
import de.qaware.qav.architecture.viewcreator.ArchitectureViewCreator
import de.qaware.qav.architecture.viewcreator.Result
import de.qaware.qav.doc.QavCommand
import de.qaware.qav.doc.QavPluginDoc
import de.qaware.qav.graph.api.DependencyGraph
import de.qaware.qav.graph.filter.AndFilter
import de.qaware.qav.graph.filter.NodePropertyExistsFilter
import de.qaware.qav.graph.filter.NodePropertyInFilter
import de.qaware.qav.graph.filter.NotFilter
import groovy.util.logging.Slf4j

/**
 * QAvalidator language constructs for dealing with architecture concepts.
 *
 * @author QAware GmbH
 */
@Slf4j
@QavPluginDoc(name = "ArchitectureQavPlugin",
        description = """
                Operations offered by the ArchitectureQavPlugin:

                * Read an Architecture:
                  ** Read an Architecture DSL file
                  ** Use the Package Structure as Architecture View
                * Working with Architecture Views
                * Create an Architecture View
                """
)
class ArchitectureQavPlugin extends BasePlugin {

    private Map<String, Architecture> architectureMap = [:]

    @Override
    void apply(Analysis analysis) {
        super.apply(analysis)

        analysis.register("readArchitecture", this.&readArchitecture)
        analysis.register("architecture", this.&getArchitecture)
        analysis.register("createPackageArchitectureView", this.&createPackageArchitectureView)
        analysis.register("createArchitectureView", this.&createArchitectureView)
        analysis.register("mapDependencies", this.&mapDependencies)
        analysis.register("addArchitecture", this.&addArchitecture)
    }

    /**
     * Returns the {@link Architecture} registered under the given name.
     *
     * @param name The name.
     * @return The {@link Architecture}, or null if no architecture is registered under that name.
     */
    @QavCommand(name = "architecture",
            description = """
                            QAvalidator stores all Architectures centrally under a unique name.
                            For an architecture that is defined using the DSL, that name is defined in the DSL.
                            For an architecture derived from the actual package hierarchy, it is "Package". 
                             
                            This command returns the {@link Architecture} registered under the given name,
                            or null if no architecture is registered under that name.
                          """,
            parameters = @QavCommand.Param(name = "name", description = "The name."),
            result = "The {@link Architecture} or null."
    )
    Architecture getArchitecture(String name) {
        Architecture result = this.architectureMap[name]
        if (!result) {
            analysis.error("Architecture ${name} not found.")
        }
        return result
    }

    /**
     * Reads an {@link Architecture} from the given file name and registers it under the name defined in the file.
     *
     * @param filename the name of the architecture file
     */
    @QavCommand(name = "readArchitecture",
            description = """
                        Reads an {@link Architecture} from the given file name and registers it under the name defined in the file.
                        The architecture is then available using the `architecture(<name>)` command.
                        """,
            parameters = @QavCommand.Param(name = "filename",
                    description = """
                        The filename. It can refer to a file in the file system or in the classpath, using the
                        prefix `classpath://`. 
                        
                        If there the path is relative, then QAvalidator also searches for the architecture DSL file 
                        in the same place as the analysis DSL file. This is useful if the analysis file and the 
                        architecture file live in the same source tree.
                        """)
    )
    void readArchitecture(String filename) {
        def reader = new QavArchitectureReader(filename, analysis.context.analysisBasePath)
        reader.read()
        this.architectureMap.putAll(reader.getArchitectures())
    }

    /**
     * Uses the package structure to create a new {@link Architecture} based on the package names.
     *
     * That package architecture is available to the Analysis script under the name "Package", i.e. by using
     * <tt>architecture("Package")</tt>.
     *
     * Then, this command uses `createArchitectureView` to create the architecture view, and returns the filtered graph.
     *
     * @param graph the {@link DependencyGraph}
     * @param maxDepth the maximum depth of the package names. Defaults to <tt>0</tt>
     * @return A filtered graph which contains all nodes which belong to this new architecture.
     */
    @QavCommand(name = "createPackageArchitectureView",
            description = """
                    Uses the package structure to create a new {@link Architecture} based on the package names.
                    
                    That package architecture is available to the Analysis script under the name `Package`, i.e. by 
                    using `architecture("Package")`.
                    
                    If the parameter `maxDepth` is given and greater than 0, the package names will have `maxDepth` 
                    levels at most; the name of the architecture is `Package-n` with n being `maxDepth`.
                    
                    Then, this command uses `createArchitectureView` to create the architecture view, and returns the
                    filtered graph.
                    """,
            parameters = [
                    @QavCommand.Param(name = "graph", description = "the {@link DependencyGraph}"),
                    @QavCommand.Param(name = "maxDepth", description = "the maximum depth of the package names. Defaults to `0`, i.e. unlimited."),
            ],
            result = "A filtered graph which contains all nodes which belong to this new architecture."
    )
    DependencyGraph createPackageArchitectureView(DependencyGraph graph, int maxDepth = 0) {
        Architecture packageArchitecture = new DefaultPackageArchitectureFactory(graph).createArchitecture(maxDepth)
        this.architectureMap[packageArchitecture.name] = packageArchitecture
        return createArchitectureView(graph, packageArchitecture)
    }

    /**
     * Uses the node name to create a new {@link Architecture}.
     *
     * That architecture is available to the Analysis script under the given name, i.e. by using architecture(architectureName)</tt>.
     *
     * Then, this command uses `createArchitectureView` to create the architecture view, and returns the filtered graph.
     *
     * @param graph the {@link DependencyGraph}
     * @param architectureName the name of the architecture. Defaults to "Package"
     * @param separator the String to join the names again. Defaults to "."
     * @param maxDepth the maximum depth of the package names. Defaults to <tt>0</tt>
     * @return A filtered graph which contains all nodes which belong to this new architecture.
     */
    @QavCommand(name = "createPackageArchitectureView",
            description = """
                    Create a new {@link Architecture} based on the item names.
                    The parameter `separator` defines how to split the names.
                                        
                    That architecture is available to the Analysis script under the given name, i.e. by using e.g. `architecture("Package")`.
                    
                    If the parameter `maxDepth` is given and greater than 0, the names will have `maxDepth` levels at 
                    most; the name of the architecture is `Name-n` with n being `maxDepth`.
                    
                    Then, this command uses `createArchitectureView` to create the architecture view, and returns the
                    filtered graph.
                    
                    This command is useful to create an Architecture View e.g. based on the Package names of the classes.
                    """,
            parameters = [
                    @QavCommand.Param(name = "graph", description = "the {@link DependencyGraph}"),
                    @QavCommand.Param(name = "architectureName", description = "the name of the architecture."),
                    @QavCommand.Param(name = "separator", description = "the String to split and join the names again."),
                    @QavCommand.Param(name = "maxDepth", description = "the maximum depth of the package names. Defaults to `0`, i.e. unlimited."),
            ],
            result = "A filtered graph which contains all nodes which belong to this new architecture."
    )
    DependencyGraph createPackageArchitectureView(DependencyGraph graph, String architectureName, String separator, int maxDepth = 0) {
        def factory = new DefaultPackageArchitectureFactory(graph)
        factory.setArchitectureName(architectureName)
        factory.setPathSeparator(separator)
        Architecture architecture = factory.createArchitecture(maxDepth)
        this.architectureMap[architecture.name] = architecture
        return createArchitectureView(graph, architecture)
    }

    /**
     * Creates a new architecture view in the given {@link DependencyGraph}.
     *
     * @param sourceGraph the graph from which we create the new architecture view.
     * @param architecture the {@link Architecture} which defines the view.
     * @param tag the tag to write on every node on the given sourceGraph and on each parent architecture node;
     *            if this is null or not given, the tag defaults to the architecture name.
     *
     * @return A filtered graph which contains all nodes which belong to this architecture view (i.e. filtered according
     *         to the given architecture).
     */
    @QavCommand(name = "createArchitectureView",
            description = """
                    Creates a new architecture view in the given {@link DependencyGraph}.
                    The result is a `DependencyGraph` which is a filtered version of the incoming `sourceGraph`.
                    """,
            parameters = [
                    @QavCommand.Param(name = "sourceGraph", description = "the graph from which we create the new architecture view."),
                    @QavCommand.Param(name = "architecture", description = "the {@link Architecture} which defines the view."),
                    @QavCommand.Param(name = "tag", description = """
                            The tag to write on every node on the given sourceGraph and on each parent architecture node.
                            This parameter is optional: It defaults to the architecture name.
                            """),
            ],
            result = "A filtered graph which contains all nodes which belong to this new architecture."
    )
    DependencyGraph createArchitectureView(DependencyGraph sourceGraph, Architecture architecture, String tag = null) {
        Result result = ArchitectureViewCreator.createArchitectureView(sourceGraph, architecture, tag)

        if (result.violationMessage) {
            analysis.violation(result.violationMessage)
        }

        return result.architectureGraph
    }

    /**
     * Maps all dependencies in the given {@link DependencyGraph} one level "up" in the given {@link Architecture},
     * and returns the given graph, filtered so that it only contains the architecture nodes, but not the base nodes.
     *
     * So this methods helps to reduce a given {@link DependencyGraph}.
     *
     * @param dependencyGraph the {@link DependencyGraph}
     * @param architecture the {@link Architecture}
     * @return a {@link DependencyGraph} representing the given {@link Architecture}; the graph contains the
     *         dependencies on its lowest level, i.e. on the leaves of the architecture tree.
     */
    @QavCommand(name = "mapDependencies",
            description = """
                    Maps all dependencies in the given {@link DependencyGraph} one level "up" in the given {@link Architecture},
                    and returns the given graph, filtered so that it only contains the architecture nodes, but not the base nodes.

                    So this command helps to reduce a given {@link DependencyGraph}.
                    """,
            parameters = [
                    @QavCommand.Param(name = "dependencyGraph", description = "the {@link DependencyGraph}"),
                    @QavCommand.Param(name = "architecture", description = "the {@link Architecture}")
            ],
            result = """
                     The result is a `DependencyGraph` representing the given Architecture; the graph contains the
                     dependencies on its lowest level, i.e. on the leaves of the architecture tree.
                    """
    )
    DependencyGraph mapDependencies(DependencyGraph dependencyGraph, Architecture architecture) {
        ArchitectureHeightTagger.tagArchitectureHeight(dependencyGraph, architecture)
        String heightTagName = architecture.getName() + "-height"
        DependencyGraph baseComponentGraph = dependencyGraph.filter(new NodePropertyInFilter(heightTagName, 1))

        createArchitectureView(baseComponentGraph, architecture, architecture.name)
        return dependencyGraph.filter(
                new AndFilter(
                        new NodePropertyExistsFilter(heightTagName),
                        new NotFilter(new NodePropertyInFilter(heightTagName, 0))
                ))
    }

    /**
     * Add an architecture.
     *
     * @param architecture the {@link Architecture}
     */
    @QavCommand(name = "addArchitecture",
            description = "Add an architecture. This command is useful for other plugins; it will probably not be used in Analysis DSL scripts.",
            parameters = @QavCommand.Param(name = "architecture", description = "the {@link Architecture}")
    )
    void addArchitecture(Architecture architecture) {
        this.architectureMap[architecture.name] = architecture
    }
}
