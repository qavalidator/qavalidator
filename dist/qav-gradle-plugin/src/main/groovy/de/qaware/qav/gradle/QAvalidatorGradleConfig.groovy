package de.qaware.qav.gradle

/**
 * Configuration bean for the QAvalidator Gradle plugin.
 *
 * @author QAware GmbH
 */
class QAvalidatorGradleConfig {

    /**
     * <tt>true</tt> to use the classes directories as defined in the build.
     * <tt>false</tt> to define the input directories in the analysis DSL file.
     */
    boolean useDefaultInputDirs = true

    /**
     * <tt>true</tt> to break the build on an error,
     * <tt>false</tt> to print out warnings.
     */
    boolean failOnError = false

    /**
     * the analysis file name.
     * If left empty, QAvalidator will try its default values.
     */
    String analysisFilename = ""

    /**
     * output directory.
     * May be overridden. Defaults to "build/qav-report".
     */
    String outputDir = null

    // --- Properties for the Server

    /**
     * Port to run the QAvalidator Server
     */
    String port = '8080'

    /**
     * Graph file to read and serve
     */
    String graph = 'build/qav-report/dependencyGraph.json'
}
