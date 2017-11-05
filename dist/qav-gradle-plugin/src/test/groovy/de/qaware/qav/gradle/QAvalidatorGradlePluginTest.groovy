package de.qaware.qav.gradle

import de.qaware.qav.runner.QAvalidatorConfig
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.hamcrest.Matchers.endsWith
import static org.hamcrest.Matchers.notNullValue
import static org.hamcrest.Matchers.is
import static org.junit.Assert.assertThat

/**
 * @author QAware GmbH
 */
class QAvalidatorGradlePluginTest {

    @Test
    void testApply() {
        Project project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'de.qaware.qav.gradle.qavalidator'

        def qavTask = project.tasks.getByName("qavalidator")
        assertThat(qavTask, notNullValue())

        Boolean plugin = project.getPluginManager().hasPlugin("de.qaware.qav.gradle.qavalidator")
        assert plugin
    }

    @Test
    void testConfiguration() {
        Project project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'de.qaware.qav.gradle.qavalidator'
        project.qavalidator {
            failOnError = true
            analysisFilename = "not-existing-analysis.groovy"
        }

        QAvalidatorGradlePlugin qavPlugin = project.getPlugins().findPlugin('de.qaware.qav.gradle.qavalidator') as QAvalidatorGradlePlugin
        assertThat(qavPlugin, notNullValue())

        QAvalidatorConfig config = qavPlugin.createConfig(project)
        QAvalidatorGradleConfig gradleConfig = qavPlugin.getConfig()

        assertThat(config, notNullValue())
        assertThat(gradleConfig, notNullValue())
        assert gradleConfig.getFailOnError()
        assert gradleConfig.getAnalysisFilename() == "not-existing-analysis.groovy" // original value
        assert config.getAnalysisFilename() == "not-existing-analysis.groovy" // used because it's explicitly defined
        assertThat(config.getOutputDir(), endsWith("build/qav-report"))
    }

    @Test
    void testConfiguration2() {
        Project project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'de.qaware.qav.gradle.qavalidator'
        project.qavalidator {
            failOnError = true
            useDefaultInputDirs = false
            analysisFilename = "src/test/resources/empty-analysis.groovy"
            outputDir = "/tmp"
        }

        QAvalidatorGradlePlugin qavPlugin = project.getPlugins().findPlugin('de.qaware.qav.gradle.qavalidator') as QAvalidatorGradlePlugin
        assertThat(qavPlugin, notNullValue())

        QAvalidatorConfig config = qavPlugin.createConfig(project)
        QAvalidatorGradleConfig gradleConfig = qavPlugin.getConfig()

        assertThat(config, notNullValue())
        assertThat(gradleConfig, notNullValue())
        assert gradleConfig.getFailOnError()
        assert !gradleConfig.getUseDefaultInputDirs()

        // do some gymnastic because the configuration has an absolute path
        assert new File(config.getAnalysisFilename()).absolutePath == new File("src/test/resources/empty-analysis.groovy").absolutePath
        assertThat(config.getOutputDir(), is("/tmp"))
    }
}