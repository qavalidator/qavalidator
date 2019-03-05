package de.qaware.qav.app;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assume;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This is not a unit test, but a test run on the complete source.
 *
 * @author QAware GmbH
 */
@Slf4j
public class QavIntegrationTest {

    /**
     * This is a sample to run on QAvalidator.
     */
    @Test
    public void testOnSelf() {
        String codebase = getCodebaseDir();
        Assume.assumeTrue("Directory " + codebase + " doesn't exist.", new File(codebase).exists());

        QavMain.main("--analysis=classpath:/default_analysis.groovy", "--outputDir=build/results-self",
                codebase + "/qav-app/build/classes/java/main");

        assertThat(new File("build/results-self").exists()).isTrue();
    }

    private String getCodebaseDir() {
        try {
            String result = new File(".").getCanonicalFile().getParentFile().getAbsolutePath();
            LOGGER.info("Trying directory {}", result);
            return result;
        } catch (IOException e) {
            throw new IllegalStateException("Can't get canonical path", e);
        }
    }

    /**
     * This is a sample to run on QAvalidator on Typescript input.
     */
    @Test
    public void testTypescript() {
        QavMain.main("--analysis=src/test/qa/analysis-typescript.groovy", "--outputDir=build/results-typescript");
        assertThat(new File("build/results-typescript").exists()).isTrue();
    }

    /**
     * Tests that <tt>main</tt> catches all exceptions in the analysis.
     */
    @Test
    public void testErrorHandling() {
        QavMain.main("--analysis=classpath:/error_architecture.groovy", "--outputDir=build/results-error");
        assertThat(new File("build/results-error").exists()).isTrue();
    }
}
