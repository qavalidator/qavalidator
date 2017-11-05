package de.qaware.qav.analysis.dsl.api

import de.qaware.qav.analysis.dsl.impl.QavAnalysisDSL
import de.qaware.qav.graph.factory.DependencyGraphFactory
import de.qaware.qav.util.FileNameUtil
import de.qaware.qav.util.FileSystemUtil
import groovy.util.logging.Slf4j
import org.codehaus.groovy.control.CompilationFailedException
import org.codehaus.groovy.control.CompilerConfiguration

/**
 * Initializes the QAvalidator analysis DSL, and applies the standard plugins.
 * Reads a QAvalidator analysis definition file.
 *
 * @author QAware GmbH
 */
@Slf4j
class QavAnalysisReader {

    List<String> inputDirs
    String outputDir
    Expando context

    private GroovyShell shell
    private String analysisFileText
    private String analysisFileName

    /**
     * Constructor.
     *
     * @param analysisFileName name of the analysis file
     */
    QavAnalysisReader(String analysisFileName) {
        this.analysisFileName = analysisFileName
        log.info("Reading analysis file: {}", FileNameUtil.getCanonicalPath(analysisFileName))
        this.analysisFileText = FileSystemUtil.readFileAsText(analysisFileName)
    }

    /**
     * Read the given QAvalidator Analysis File.
     *
     * If {@link #inputDirs} or {@link #outputDir} was given, it will be used just as if it would have been defined in
     * the QAvalidator Analysis File.
     */
    void read() {
        init()

        shell.evaluate("initScript()")

        try {
            shell.evaluate(analysisFileText)

            // add input dirs and override output dir:
            if (inputDirs) {
                inputDirs.each {dir ->
                    dir = fixPath(dir)
                    shell.evaluate("inputJava('${dir}')")
                }
            }
            if (outputDir) {
                outputDir = fixPath(outputDir)
                shell.evaluate("outputDir('${outputDir}')")
            }
        }
        catch (CompilationFailedException e) {
            throw new IllegalArgumentException("Compilation error: " + e, e)
        }
        catch (Throwable e) {
            shell.setVariable("exceptionObj", e)
            shell.evaluate("error(exceptionObj)")
            shell.setVariable("exceptionObj", null)
        }
        shell.evaluate("finishScript()")
    }

    private void init() {
        this.context = new Expando()
        this.context.dependencyGraph = DependencyGraphFactory.createGraph()
        this.context.analysisBasePath = FileNameUtil.getParentPath(this.analysisFileName)

        def conf = new CompilerConfiguration()
        conf.setScriptBaseClass(QavAnalysisDSL.name)

        def binding = new Binding()
        binding.context = this.context

        // The Gradle plugin requires the classloader to be set here.
        // Otherwise, the Groovy Shell can't access the class QavAnalysisDSL.
        this.shell = new GroovyShell(this.class.classLoader, binding, conf)
    }

    private static String fixPath(String path) {
        return path.replaceAll("\\\\", "/")
    }
}
