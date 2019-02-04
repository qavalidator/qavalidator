package de.qaware.qav.analysis.result.api;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * Tests for {@link AnalysisResultReader}.
 * <p>
 * See also the test for the {@link AnalysisResultWriter} in {@link AnalysisResultWriterTest} which does a basic
 * roundtrip test.
 */
public class AnalysisResultReaderTest {

    @Test
    public void testNoFile() {
        try {
            new AnalysisResultReader("not/existing/file");
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).startsWith("File not found:");
        }
    }

    @Test
    public void testNoJsonFile() {
        try {
            new AnalysisResultReader("src/test/resources/analysis-result/no-json.txt");
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).isEqualTo("Error reading file src/test/resources/analysis-result/no-json.txt");
        }
    }

}