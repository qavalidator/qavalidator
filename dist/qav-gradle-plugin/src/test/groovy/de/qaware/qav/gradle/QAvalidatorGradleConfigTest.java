package de.qaware.qav.gradle;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link QAvalidatorGradleConfig}.
 *
 * @author QAware GmbH
 */
public class QAvalidatorGradleConfigTest {

    @Test
    public void testDefaults() {
        QAvalidatorGradleConfig config = new QAvalidatorGradleConfig();

        assertThat(config.getUseDefaultInputDirs(), is(true));
        assertThat(config.getFailOnError(), is(false));
        assertThat(config.getAnalysisFilename(), is(""));
        assertThat(config.getOutputDir(), nullValue());
    }
}