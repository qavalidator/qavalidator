package de.qaware.qav.gradle

import de.qaware.qav.app.server.QavServerConfiguration
import de.qaware.qav.runner.QAvalidator
import de.qaware.qav.runner.QAvalidatorConfig
import de.qaware.qav.runner.QAvalidatorResult
import org.apache.catalina.webresources.TomcatURLStreamHandlerFactory
import org.apache.commons.lang3.StringUtils
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.springframework.boot.SpringApplication

/**
 * QAvalidator Gradle Plugin.
 *
 * Execute QAvalidator in a Gradle run.
 *
 * @author QAware GmbH
 */
class QAvalidatorGradlePlugin implements Plugin<Project> {

    private static final Logger LOGGER = Logging.getLogger(QAvalidatorGradlePlugin)

    private QAvalidatorGradleConfig config

    @Override
    void apply(Project project) {
        project.extensions.create("qavalidator", QAvalidatorGradleConfig)

        project.task('qavalidator') {
            description = "Runs a QAvalidator analysis on this project"
            group = "QAvalidator"

            doLast {
                QAvalidatorConfig config = createConfig(project)
                QAvalidatorResult result = new QAvalidator().runAnalysis(config)
                reportResult(result)
            }
        }

        project.task('qavalidatorServer') {
            description = "Runs the QAvalidator Server for using the Web UI"
            group = "QAvalidator"

            doLast {
                this.config = project.qavalidator
                checkFileAvailable(this.config.graph)
                System.setProperty("graph", this.config.graph)
                System.setProperty("server.port", this.config.port)

                // URL.setURLStreamHandlerFactory can only ever be called once per JVM instance
                // call this to avoid calling it a second time:
                TomcatURLStreamHandlerFactory.disable()

                SpringApplication.run(QavServerConfiguration.class)

                waitForKill()
            }
        }
    }

    /**
     * Set up the configuration for the QAvalidator run.
     * <p>
     * Apply default strategies to deal with empty or invalid configuration values.
     *
     * @param project the Gradle {@link Project}
     * @return the {@link QAvalidatorConfig}
     */
    protected QAvalidatorConfig createConfig(Project project) {
        this.config = project.qavalidator
        QAvalidatorConfig config = new QAvalidatorConfig()

        config.setAnalysisFilename(getAnalysisFile())
        config.setInputDirs(getInputDirs(project))
        config.setOutputDir(getOutputDir(project))

        logConfig(config)

        return config
    }

    /**
     * Sets the analysis file.
     */
    private String getAnalysisFile() {
        return this.config.analysisFilename
    }

    /**
     * Sets the input directories, if <tt>useDefaultInputDirs</tt> is <tt>true</tt>.
     *
     * @param project the Gradle {@link Project}
     */
    private List<String> getInputDirs(Project project) {
        if (this.config.useDefaultInputDirs) {
            ArrayList<String> classesDirs = getClassesDirs(project)
            return classesDirs
        } else {
            LOGGER.warn("Not using default classes dirs. Set the input explicitly in the analysis file!")
            return []
        }
    }

    /**
     * get all classes directories from this project and all its sub projects
     *
     * @param project the Gradle {@link Project}
     * @return the list of all classes directory names
     */
    private static ArrayList<String> getClassesDirs(Project project) {
        List<String> classesDirs = []
        addClassesDir(classesDirs, new File(project.getBuildDir(), "classes/main"))
        project.subprojects.each {
            addClassesDir(classesDirs, new File(it.getBuildDir(), "classes/main"))
        }
        classesDirs
    }

    private static void addClassesDir(List<String> classesDirs, File dir) {
        if (dir.exists()) {
            classesDirs << dir.absolutePath
        }
    }

    private String getOutputDir(Project project) {
        return StringUtils.defaultIfBlank(this.config.outputDir, project.getBuildDir().getAbsolutePath() + "/qav-report")
    }

    /**
     * reports the result: either write a warning, or fail the build.
     *
     * @param result the {@link QAvalidatorResult}
     */
    private void reportResult(QAvalidatorResult result) {
        if (result.failedWithException) {
            reportError("The QAvalidator analysis failed with an exception: " + result.exceptionMessage)
        }
        if (!result.failedSteps.isEmpty()) {
            reportError("The QAvalidator analysis failed in the following steps: " + result.failedSteps.toString())
        }
    }

    /**
     * reports the result: either write a warning, or fail the build.
     *
     * @param message the message to print
     */
    private void reportError(String message) {
        if (this.config.failOnError) {
            throw new GradleException(message)
        } else {
            LOGGER.warn(message)
        }
    }

    /**
     * Getter.
     *
     * Only used for testing.
     *
     * @return the {@link QAvalidatorGradleConfig} object
     */
    protected QAvalidatorGradleConfig getConfig() {
        return config
    }

    /**
     * Logs the configuration, after applying the default strategies.
     *
     * @param config the configuration
     */
    protected void logConfig(QAvalidatorConfig config) {
        LOGGER.info "QAvalidator configuration:"
        LOGGER.info "    * analysisFilename: ${config.analysisFilename}"
        LOGGER.info "    * inputDirs: ${config.inputDirs}"
        LOGGER.info "    * outputDir: ${config.outputDir}"
        LOGGER.info "QAvalidator Gradle Plugin configuration:"
        LOGGER.info "    * failOnError: ${this.config.failOnError}"
        LOGGER.info "QAvalidator Server configuration:"
        LOGGER.info "    * port: ${this.config.port}"
        LOGGER.info "    * graph: ${this.config.graph}"
    }

    private static void checkFileAvailable(String graphFilename) {
        File graphFile = new File(graphFilename)
        if (!graphFile.exists()) {
            String msg = "File " + graphFile.getAbsolutePath() + " not found."
            LOGGER.error(msg)
            throw new IllegalArgumentException(msg)
        }
    }

    /**
     * Wait until stopped with Ctrl-C
     */
    private static void waitForKill() {
        try {
            Thread.currentThread().join()
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted", e)
        }
    }
}

