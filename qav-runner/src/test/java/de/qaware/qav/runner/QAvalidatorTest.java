package de.qaware.qav.runner;

import com.google.common.collect.Lists;
import org.junit.Test;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Tests for {@link QAvalidator}
 *
 * @author QAware GmbH
 */
public class QAvalidatorTest {

    @Test
    public void testRunAnalysis() {

        QAvalidatorConfig config = new QAvalidatorConfig();
        config.setAnalysisFilename("classpath:/default_analysis.groovy");
        config.setInputDirs(Lists.newArrayList("./build/classes/java/main"));
        config.setOutputDir("./build/result-default-architecture");

        QAvalidatorResult result = new QAvalidator().runAnalysis(config);

        assertThat(result, notNullValue());
        assertThat(result.getFailedSteps(), hasSize(0));
        assertThat(result.isFailedWithException(), is(false));
        assertThat(result.getExceptionMessage(), nullValue());
    }

    @Test
    public void testWithNotExistingFile() {
        QAvalidatorConfig config = new QAvalidatorConfig();
        config.setAnalysisFilename("not/existing/file");

        QAvalidatorResult result = new QAvalidator().runAnalysis(config);

        assertThat(result, notNullValue());
        assertThat(result.getFailedSteps(), hasSize(0));
        assertThat(result.isFailedWithException(), is(true));
        assertThat(result.getExceptionMessage(), startsWith("Can not find analysis file"));
        assertThat(result.getExceptionMessage().replaceAll("\\\\", "/"), endsWith("not/existing/file"));
    }

    @Test
    public void testMultipleInputFilesNotGiven() {
        QAvalidatorConfig config = new QAvalidatorConfig();
        config.setAnalysisFilename("");

        String filename = new QAvalidator().selectAnalysisFilename(config);
        assertThat(filename, is("src/qa/analysis.groovy"));
    }

    @Test
    public void testMultipleInputFilesNotFound() {
        QAvalidatorConfig config = new QAvalidatorConfig();
        config.setAnalysisFilename("/not/existing/file");

        try {
            new QAvalidator().selectAnalysisFilename(config);
            fail("IllegalArgumentException expected");
        } catch(IllegalArgumentException e) {
            assertThat(e.getMessage(), startsWith("Can not find analysis file "));
            assertThat(e.getMessage().replaceAll("\\\\", "/"), endsWith("/not/existing/file"));
        }
    }
}
