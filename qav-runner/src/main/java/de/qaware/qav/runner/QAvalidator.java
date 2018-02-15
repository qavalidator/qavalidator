package de.qaware.qav.runner;

import de.qaware.qav.analysis.dsl.api.QavAnalysisReader;
import de.qaware.qav.util.FileSystemUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * API to QAvalidator.
 *
 * @author QAware GmbH
 */
public class QAvalidator {

    /**
     * Defaults, if no analysis filename is given.
     */
    private static final String[] ANALYSIS_FILENAME_FALLBACKS = new String[]{
            "qa/analysis.groovy",
            "src/qa/analysis.groovy",
            "classpath:/default_analysis.groovy"
    };

    /**
     * Runs a QAvalidator analysis.
     * <p>
     * Does the exception handling.
     *
     * @param config the configuration
     * @return the result of the run. If the analysis fails with any kind of {@link Exception}, a flag is set and the
     * message is contained in the {@link QAvalidatorResult}.
     */
    @SuppressWarnings({"squid:S2221","squid:S1166"})
    // catching Exception (instead of specific subtypes) because there could also be runtime exceptions.
    // Their messages will all be properly logged.
    public QAvalidatorResult runAnalysis(QAvalidatorConfig config) {

        QAvalidatorResult result = new QAvalidatorResult();

        try {

            String analysisFilename = selectAnalysisFilename(config);
            QavAnalysisReader qavAnalysisReader = new QavAnalysisReader(analysisFilename); // may throw IllegalArgumentException
            qavAnalysisReader.setInputDirs(config.getInputDirs());
            qavAnalysisReader.setOutputDir(config.getOutputDir());

            qavAnalysisReader.read(); // actually runs the analysis

            @SuppressWarnings("unchecked")
            List<String> failedSteps = (List<String>) qavAnalysisReader.getContext().getProperty("failedSteps");
            result.setFailedSteps(failedSteps);

        } catch (Exception e) {
            result.setFailedWithException(true);
            result.setExceptionMessage(e.getMessage());
        }

        return result;
    }

    /**
     * Find the analysis filename to use.
     * <p>
     * First checks the {@link QAvalidatorConfig}: If there is an analysis filename given, that file should be used and
     * must therefore exist.
     * <p>
     * If not filename is given in the config, it tries the default locations, as defined in {@link
     * #ANALYSIS_FILENAME_FALLBACKS}.
     *
     * @param config the {@link QAvalidatorConfig}
     * @return the analysis filename
     * @throws IllegalArgumentException if the analysis file can't be found
     */
    protected String selectAnalysisFilename(QAvalidatorConfig config) {
        if (StringUtils.isNotBlank(config.getAnalysisFilename())) {
            if (FileSystemUtil.checkFileOrResourceExists(config.getAnalysisFilename())) {
                return config.getAnalysisFilename();
            } else {
                throw new IllegalArgumentException("Can not find analysis file " + config.getAnalysisFilename());
            }
        } else {
            return findFirstExistingDefault();
        }
    }

    private String findFirstExistingDefault() {
        for (String filename : ANALYSIS_FILENAME_FALLBACKS) {
            if (FileSystemUtil.checkFileOrResourceExists(filename)) {
                return filename;
            }
        }

        // This can't happen, as the default analysis is always on the classpath.
        throw new IllegalArgumentException("Can not find any analysis file in " + Arrays.toString(ANALYSIS_FILENAME_FALLBACKS));
    }
}
