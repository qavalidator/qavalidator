package de.qaware.qav.analysis.result.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qaware.qav.analysis.result.model.AnalysisResult;
import de.qaware.qav.analysis.result.model.Result;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Stores analysis results and writes them into a file.
 */
@Slf4j
public class AnalysisResultWriter {

    public static final String QAV_RESULT_FILENAME = "qav-analysis-result.json";

    @Getter
    private final AnalysisResult analysisResult;
    private final ObjectMapper mapper;

    private File outputFile;

    /**
     * Constructor.
     */
    public AnalysisResultWriter() {
        analysisResult = new AnalysisResult();
        analysisResult.setAnalysisId("qav-" + UUID.randomUUID().toString());
        analysisResult.setAnalysisStart(LocalDateTime.now());

        mapper = ResultJsonUtil.initJsonMapper();
    }

    /**
     * Set the output directory. This is the place where the output file will be written.
     * <p>
     * See the filename definition here: {@link #QAV_RESULT_FILENAME} and the output file here: {@link #outputFile}.
     *
     * @param outputDir the output directory
     */
    public void setOutputDir(String outputDir) {
        outputFile = new File(outputDir, QAV_RESULT_FILENAME);
        analysisResult.setBaseDir(outputDir);
        LOGGER.info("Using output file: {}", outputFile.getAbsolutePath());
    }

    /**
     * Add a {@link Result} item to the {@link #analysisResult}.
     * <p>
     * Write the complete {@link #analysisResult} to file. This may not be the most efficient way, but it's easier,
     * safer, and it's only small file, so it does not really hurt.
     *
     * @param result the new {@link Result} to add
     */
    public void addResult(Result result) {
        LOGGER.debug("Add result: {}", result);
        this.analysisResult.addResult(result);
        writeResult();
    }

    /**
     * Writes the current state of the {@link #analysisResult} into the output file.
     * <p>
     * Creates it if it does not exist; overwrites it if it does.
     *
     * @throws IllegalStateException if writing the JSON file fails.
     */
    private void writeResult() {
        try {
            mapper.writeValue(outputFile, analysisResult);
        } catch (IOException e) {
            throw new IllegalStateException("Error writing JSON to file: ", e);
        }
    }
}
