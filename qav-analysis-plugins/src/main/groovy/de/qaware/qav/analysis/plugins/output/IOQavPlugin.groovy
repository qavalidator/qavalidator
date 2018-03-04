package de.qaware.qav.analysis.plugins.output

import de.qaware.qav.analysis.dsl.model.Analysis
import de.qaware.qav.analysis.plugins.base.BasePlugin
import de.qaware.qav.analysis.plugins.output.impl.SonarLogUtil
import de.qaware.qav.architecture.dsl.model.Architecture
import de.qaware.qav.doc.QavCommand
import de.qaware.qav.doc.QavPluginDoc
import de.qaware.qav.graph.api.DependencyGraph
import de.qaware.qav.graph.io.GraphReaderWriter
import de.qaware.qav.graph.io.NodePrinter
import de.qaware.qav.visualization.Abbreviation
import de.qaware.qav.visualization.GraphExporter
import de.qaware.qav.visualization.LegendCreator
import groovy.util.logging.Slf4j
import org.apache.commons.io.FileUtils

/**
 * QAvalidator Language elements for Input / Output / Beautifying.
 *
 * @author QAware GmbH
 */
@QavPluginDoc(name = "IOQavPlugin",
        description = "QAvalidator Language elements for Input/Output, writing JSON, DOT, or text-format Node infos")
@Slf4j
class IOQavPlugin extends BasePlugin {

    private String outputDir = './'
    private boolean outputDirDefined = false
    private List abbreviations = []

    @Override
    void apply(Analysis analysis) {
        super.apply(analysis)

        analysis.register("abbreviation", this.&registerAbbreviation)
        analysis.register("printNodes", this.&printNodes)
        analysis.register("writeFile", this.&writeFile)
        analysis.register("writeDot", this.&writeDot)
        analysis.register("writeGraphLegend", this.&writeGraphLegend)
        analysis.register("outputDir", this.&setOutputDir)
    }

    /**
     * Registers a new abbreviation which will be used for DOT exports.
     *
     * @param longName the long name to abbreviate
     * @param shortName the abbreviation
     */
    @QavCommand(name = "abbreviation",
            description = "Registers a new abbreviation which will be used for DOT exports.",
            parameters = [
                    @QavCommand.Param(name = "longName", description = "the long name to abbreviate"),
                    @QavCommand.Param(name = "shortName", description = "the abbreviation")
            ])
    void registerAbbreviation(String longName, String shortName) {
        this.abbreviations << new Abbreviation(longName, shortName)
    }

    /**
     * Prints the given node of the given graph into a file.
     *
     * @param dependencyGraph the graph
     * @param filename the filename; it is relative to the outputDir defined via {@link #setOutputDir(java.lang.String)}
     * @param printIfEmpty if <tt>true</tt>, writes the file even if the graph is empty
     */
    @QavCommand(name = "printNodes",
            description = """
                    Prints the given node of the given graph into a file.
                    
                    TIP: Use filters to print only the relevant nodes and edges.
                    """,
            parameters = [
                    @QavCommand.Param(name = "dependencyGraph", description = "The graph in which the nodes lie."),
                    @QavCommand.Param(name = "filename", description = """
                            The filename; it is relative to the `outputDir` defined via the `outputDir` command or
                            in the Maven/Gradle config (explicitly or by default), or on the command line.
                            """),
                    @QavCommand.Param(name = "printIfEmpty", description = """
                            if `true`, prints the (empty) file even if there are no nodes in the graph; 
                            if `false`, only prints the file if the graph actually contains nodes. Defaults to `true`.
                            """)
            ])
    void printNodes(DependencyGraph dependencyGraph, String filename, boolean printIfEmpty = true) {
        if (!dependencyGraph.getAllNodes().isEmpty() || printIfEmpty) {
            new NodePrinter(dependencyGraph, this.outputDir + "/" + filename).printNodes()
        }
    }

    /**
     * Writes the given graph to a file with the given filename in the directory defined as outputDir.
     *
     * @param dependencyGraph the graph.
     * @param filename the filename.
     */
    @QavCommand(name = "writeFile",
            description = "Writes the given graph to a file with the given filename in the directory defined as outputDir.",
            parameters = [
                    @QavCommand.Param(name = "dependencyGraph", description = "the graph"),
                    @QavCommand.Param(name = "filename", description = "the filename"),

            ])
    void writeFile(DependencyGraph dependencyGraph, String filename) {
        GraphReaderWriter.write(dependencyGraph, this.outputDir + "/" + filename)
    }

    /**
     * Writes the given graph to a DOT (GraphViz) file with the given filename in the directory defined as outputDir.
     * Then calls dot (GraphViz) to create a .png file from it.
     * Also writes a GraphML file with the same base filename, to be used with yEd.
     *
     * @param dependencyGraph the graph
     * @param filenameBase the file name base, used to set up the .dot, .png, and .graphml file names
     * @param architecture the {@link Architecture} to use to show the hierarchy of the nodes
     */
    @QavCommand(name = "writeDot",
            description = """
                        Writes the given graph to a DOT (GraphViz) file with the given filename in the directory
                        defined as outputDir.
                        
                        Then calls dot (GraphViz) to create .png and .svng files from it.
                        Also writes a GraphML file with the same base filename, to be used with yEd.
                        
                        TIP: Install GraphViz to allow for .png and .svg creation.
                        """,
            parameters = [
                    @QavCommand.Param(name = "dependencyGraph", description = "the graph"),
                    @QavCommand.Param(name = "filename", description = "the filename"),
                    @QavCommand.Param(name = "architecture", description = "the {@link Architecture} to use to show the hierarchy of the nodes."),
                    @QavCommand.Param(name = "createEdgeLabels", description = """
                            `true` to print edge labels, `false` to omit them. 
                            
                            The edge labels give the the number of base relations (at the middle of an edge), 
                            the number of different base relation sources (at the start of an edge),
                            and the number of different base relation targets (ad the end of an edge). 
                            
                            This parameter is optional. It defaults to `true`.
                            """),

            ])
    void writeDot(DependencyGraph dependencyGraph, String filenameBase, Architecture architecture, boolean createEdgeLabels = true) {
        GraphExporter.export(dependencyGraph, this.outputDir + "/" + filenameBase, architecture, this.abbreviations, createEdgeLabels)
    }

    /**
     * Writes the graph legend with the given filename base in the directory defined as outputDir.
     * Then calls dot (GraphViz) to create a .png file from it.
     * Also writes a GraphML file with the same base filename, to be used with yEd.
     *
     * This is useful to see the color code for the dependency types.
     *
     * @param filenameBase the filename base, relative to outputDir, defaults to 'legend'
     */
    @QavCommand(name = "writeGraphLegend",
            description = """
                        Writes the graph legend with the given filename base in the directory defined as outputDir.
                        Then calls dot (GraphViz) to create a .png file from it.
                        Also writes a GraphML file with the same base filename, to be used with yEd.
                        
                        This is useful to see the color code for the dependency types.
                    """,
            parameters = [
                    @QavCommand.Param(name = "filenameBase", description = "the filename base, relative to outputDir; defaults to `legend`")
            ])
    void writeGraphLegend(String filenameBase = "legend") {
        new LegendCreator().export(this.outputDir + "/" + filenameBase)
    }

    /**
     * Sets the output directory. If the "clean" flag is set to true, deletes the directory (and its content) and then
     * re-creates it.
     *
     * Throws an {@link IllegalArgumentException} if there exists a <em>file</em> (not a directory) with the given name,
     * or if the directory can't be created (as indicated by an IOException).
     *
     * Note that QAvalidator only allows to set the <tt>outputDir</tt> <em>once</em>. This is helpful if the analysis
     * DSL file defines an output directory, but the command line or the Maven pom overrides that directory.
     *
     * @param path the path
     * @param clean if true, deletes the directory completely before creating it. Defaults to false
     */
    @QavCommand(name = "outputDir",
            description = """
                        Sets the output directory. If the "clean" flag is set to true, deletes the directory (and its
                        content) and then re-creates it.

                        Throws an {@link IllegalArgumentException} if there exists a _file_ (not a directory) with the
                        given name, or if the directory can't be created (as indicated by an IOException).

                        Note that QAvalidator only allows to set the `outputDir` *once*. This is helpful if the analysis
                        DSL file defines an output directory, but the command line or the Maven pom overrides that
                        directory.
                        """,
            parameters = [
                    @QavCommand.Param(name = "path", description = "the path"),
                    @QavCommand.Param(name = "clean", description = "if true, deletes the directory completely before creating it. Defaults to `false`"),

            ])
    void setOutputDir(String path, boolean clean = false) {
        if (outputDirDefined) {
            log.info("Output dir already defined: Using ${outputDir} (ignoring ${path})")
            return
        }

        File dir = new File(path)
        if (dir.exists() && !dir.isDirectory()) {
            throw new IllegalArgumentException("${dir.absolutePath} is not a directory!")
        } else {
            try {
                if (clean) {
                    FileUtils.deleteDirectory(dir)
                }
                dir.mkdirs()
                this.outputDir = dir.canonicalPath
                log.info("Using output path ${this.outputDir}")
            } catch (IOException e) {
                throw new IllegalArgumentException("${dir.absolutePath} can't be created: ${e.getMessage()}")
            }

            outputDirDefined = true
            SonarLogUtil.setOutputDir(dir.canonicalPath) // may throw runtime exception if file can't be created.
        }
    }

    /**
     * Getter.
     *
     * @return the output directory as an absolute path.
     */
    String getOutputDir() {
        return this.outputDir
    }

    /**
     * Getter.
     *
     * @return the list of abbreviations
     */
    List getAbbreviations() {
        return abbreviations
    }
}
