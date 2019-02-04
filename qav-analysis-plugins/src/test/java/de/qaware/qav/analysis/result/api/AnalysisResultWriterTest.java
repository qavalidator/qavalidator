package de.qaware.qav.analysis.result.api;

import de.qaware.qav.analysis.result.model.AnalysisResult;
import de.qaware.qav.analysis.result.model.Result;
import de.qaware.qav.analysis.result.model.ResultType;
import de.qaware.qav.util.FileSystemUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link AnalysisResultWriter}
 */
public class AnalysisResultWriterTest {

    public static final String OUTPUT_DIR = "build/analysis-test";
    private AnalysisResultWriter resultWriter;

    @Before
    public void setup() {
        FileSystemUtil.deleteDirectoryQuietly(OUTPUT_DIR);
        new File(OUTPUT_DIR).mkdirs();

        resultWriter = new AnalysisResultWriter();
        resultWriter.setOutputDir(OUTPUT_DIR);
    }

    @Test
    public void testBasicRoundtrip() {
        resultWriter.addResult(new Result(ResultType.IMAGE, "image", 14, 32));
        resultWriter.addResult(new Result(ResultType.GRAPH, "graph.json"));

        assertThat(new File(OUTPUT_DIR).exists()).isTrue();
        assertThat(new File(OUTPUT_DIR).isDirectory()).isTrue();
        File resultFile = new File(OUTPUT_DIR, AnalysisResultWriter.QAV_RESULT_FILENAME);
        assertThat(resultFile.exists()).isTrue();

        // Read the result and check it:
        AnalysisResultReader reader = new AnalysisResultReader(resultFile.getAbsolutePath());
        AnalysisResult analysisResult = reader.getAnalysisResult();

        assertThat(analysisResult).isNotNull();
        assertThat(analysisResult.getItems()).hasSize(2);
        assertThat(analysisResult).isEqualTo(resultWriter.getAnalysisResult());

        assertThat(analysisResult.getAnalysisId()).startsWith("qav-");
        assertThat(analysisResult.getAnalysisId()).hasSize(40);
        assertThat(Duration.between(analysisResult.getAnalysisStart(), LocalDateTime.now()).getSeconds()).isLessThan(5);
    }
}