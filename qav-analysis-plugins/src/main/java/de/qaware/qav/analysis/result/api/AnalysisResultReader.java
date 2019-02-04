package de.qaware.qav.analysis.result.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qaware.qav.analysis.result.model.AnalysisResult;
import de.qaware.qav.util.FileSystemUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

/**
 * Read the analysis results from a file.
 */
@Slf4j
public class AnalysisResultReader {

    @Getter
    private final AnalysisResult analysisResult;

    /**
     * Reads the analysis result from the given file.
     *
     * @param filename the filename
     */
    public AnalysisResultReader(String filename) {
        this.analysisResult = readFile(filename);
    }

    /**
     * Reads the given file.
     *
     * @param filename the filename
     * @return the {@link AnalysisResult}
     * @throws IllegalArgumentException if the file does not exist, or if reading the file fails for other reasons.
     */
    private AnalysisResult readFile(String filename) {
        FileSystemUtil.assertFileOrResourceExists(filename);
        File file = new File(filename);

        ObjectMapper mapper = ResultJsonUtil.initJsonMapper();

        try {
            return mapper.readValue(file, AnalysisResult.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Error reading file " + filename, e);
        }
    }
}
