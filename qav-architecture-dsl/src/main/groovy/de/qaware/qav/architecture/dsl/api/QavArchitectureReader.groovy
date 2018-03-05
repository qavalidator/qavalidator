package de.qaware.qav.architecture.dsl.api

import de.qaware.qav.architecture.dsl.impl.ArchitectureDSL
import de.qaware.qav.architecture.dsl.impl.ArchitectureResolver
import de.qaware.qav.architecture.dsl.model.Architecture
import de.qaware.qav.util.FileSystemUtil
import org.codehaus.groovy.control.CompilerConfiguration

/**
 * Initializes the QAV Architecture DSL, and applies the standard plugins.
 * Reads a QAV architecture definition file.
 *
 * @author QAware GmbH
 */
class QavArchitectureReader {

    private String architectureFileText

    Map<String, Architecture> architectures = [:]

    /**
     * Constructor.
     *
     * Read the given QAV Architecture File.
     *
     * @param fileName the file to read
     */
    QavArchitectureReader(String fileName, String alternateDir) {
        this.architectureFileText = FileSystemUtil.readFileAsText(fileName, alternateDir)
        read()
    }

    /**
     * Read the given QAV Architecture File.
     */
    private void read() {
        def config = new CompilerConfiguration()
        config.scriptBaseClass = ArchitectureDSL.name

        // The Gradle plugin requires the classloader to be set here.
        // Otherwise, the Groovy Shell can't access the class QavAnalysisDSL.
        def shell = new GroovyShell(this.class.classLoader, config)

        architectures = shell.evaluate(architectureFileText)

        architectures.values().each {
            ArchitectureResolver.resolveArchitecture(it)
        }
    }
}
