package de.qaware.qav.runner;

import com.google.common.base.MoreObjects;

import java.util.List;

/**
 * Configuration bean for a QAvalidator analysis run.
 *
 * @author QAware GmbH
 */
public class QAvalidatorConfig {

    /**
     * Input directories.
     */
    private List<String> inputDirs;

    /**
     * <tt>true</tt> to break the build on an error,
     * <tt>false</tt> to print out warnings.
     */
    private boolean failOnError = false;

    /**
     * the analysis file name.
     */
    private String analysisFilename = "";

    /**
     * output directory.
     * May be overridden. Defaults to "build/qav-report".
     */
    private String outputDir = null;

    // ----- Getters and Setters

    public List<String> getInputDirs() {
        return inputDirs;
    }

    public void setInputDirs(List<String> inputDirs) {
        this.inputDirs = inputDirs;
    }

    public boolean isFailOnError() {
        return failOnError;
    }

    public void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
    }

    public String getAnalysisFilename() {
        return analysisFilename;
    }

    public void setAnalysisFilename(String analysisFilename) {
        this.analysisFilename = analysisFilename;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("inputDirs", inputDirs)
                .add("failOnError", failOnError)
                .add("analysisFilename", analysisFilename)
                .add("outputDir", outputDir)
                .toString();
    }
}
