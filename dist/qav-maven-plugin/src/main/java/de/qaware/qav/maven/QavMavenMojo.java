package de.qaware.qav.maven;

import com.google.common.collect.Lists;
import de.qaware.qav.runner.QAvalidator;
import de.qaware.qav.runner.QAvalidatorConfig;
import de.qaware.qav.runner.QAvalidatorResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Maven Plugin to start the QAV analysis in Maven build.
 *
 * @author QAware GmbH
 */
@Mojo(name = "qav", aggregator = true)
public class QavMavenMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    @Parameter(property = "qav.useDefaultInputDirs", defaultValue = "true")
    private boolean useDefaultInputDirs = true;

    @Parameter(property = "qav.failOnError", defaultValue = "false")
    private boolean failOnError;

    @Parameter(property = "qav.analysis")
    private String analysisFilename;

    @Parameter(property = "qav.outputDir")
    private String outputDir;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        QAvalidatorConfig config = createConfig();
        QAvalidatorResult result = new QAvalidator().runAnalysis(config);
        reportResult(result);
    }

    // --- setter just for testing: the Maven Harness doesn't produce completely configured Mojos.

    public void setProject(MavenProject project) {
        this.project = project;
    }

    public void setUseDefaultInputDirs(boolean useDefaultInputDirs) {
        this.useDefaultInputDirs = useDefaultInputDirs;
    }

    public void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
    }

    public void setAnalysisFilename(String analysisFilename) {
        this.analysisFilename = analysisFilename;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    /**
     * Set up the configuration for the QAvalidator run.
     * <p>
     * Apply default strategies to deal with empty or invalid configuration values.
     *
     * @return the {@link QAvalidatorConfig}
     */
    protected QAvalidatorConfig createConfig() {
        QAvalidatorConfig config = new QAvalidatorConfig();

        config.setAnalysisFilename(analysisFilename);
        config.setInputDirs(getInputDirs());
        config.setOutputDir(getOutputDir());

        logConfig(config);

        return config;
    }

    private List<String> getInputDirs() {
        if (useDefaultInputDirs) {
            return getClassesDirs();
        } else {
            getLog().info("Not using default classes dirs. Set the input explicitly in the analysis file!");
            return new ArrayList<>();
        }
    }

    private String getOutputDir() {
        return StringUtils.isBlank(outputDir)
                ? project.getBuild().getDirectory() + "/qav-report"
                : outputDir;
    }

    /**
     * Collect all the classes directories from this project and all sub projects.
     *
     * @return a List of output directories.
     */
    private List<String> getClassesDirs() {
        List<String> classesDirs = Lists.newArrayList();

        classesDirs.addAll(project.getCollectedProjects().stream()
                .map(subProject -> subProject.getBuild().getOutputDirectory())
                .collect(Collectors.toList()));
        classesDirs.add(project.getBuild().getOutputDirectory());
        return classesDirs;
    }

    private void reportResult(QAvalidatorResult result) throws MojoFailureException {
        if (!result.getFailedSteps().isEmpty()) {
            reportError("The QAV analysis failed in the following steps: " + result.getFailedSteps().toString());
        }
    }

    private void reportError(String message) throws MojoFailureException {
        if (failOnError) {
            throw new MojoFailureException(message);
        } else {
            getLog().warn(message);
        }
    }

    private void logConfig(QAvalidatorConfig config) {
        getLog().info("QAvalidator configuration:");
        getLog().info("  * analysisFilename: " + config.getAnalysisFilename());
        getLog().info("  * inputDirs: " + config.getInputDirs());
        getLog().info("  * outputDir: " + config.getOutputDir());
        getLog().info("QAvalidator Maven Plugin configuration:");
        getLog().info("  * failOnError: " + this.failOnError);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("project", project)
                .append("useDefaultInputDirs", useDefaultInputDirs)
                .append("failOnError", failOnError)
                .append("analysisFilename", analysisFilename)
                .append("outputDir", outputDir)
                .toString();
    }
}
