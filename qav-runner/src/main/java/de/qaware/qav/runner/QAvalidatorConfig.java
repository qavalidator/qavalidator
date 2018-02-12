package de.qaware.qav.runner;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * Configuration bean for a QAvalidator analysis run.
 *
 * @author QAware GmbH
 */
@ToString
@Data
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
}
