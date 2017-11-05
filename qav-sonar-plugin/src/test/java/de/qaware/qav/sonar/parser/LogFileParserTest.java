package de.qaware.qav.sonar.parser;

import org.junit.Test;

import java.io.File;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link LogFileParser}.
 *
 * @author QAware GmbH
 */
public class LogFileParserTest {


    @Test
    public void analyse1() {
        checkDirectory("src/test/resources/logTest1", 2, 1, false);
    }

    @Test
    public void analyse2() {
        checkDirectory("src/test/resources/logTest2", 3, 3, false);
    }

    @Test
    public void analyse3() {
        checkDirectory("src/test/resources/logTest3", 0, 0, true);
    }

    @Test
    public void analyse4() {
        checkDirectory("src/test/resources/logTest4", 0, 0, false);
    }

    @Test
    public void analyse5() {
        checkDirectory("src/test/resources/logTest5", 1, 0, false);
    }

    private void checkDirectory(String directory, int expectedErrors, int expectedWarnings, boolean expectedEmpty) {
        File baseDir = new File(directory);
        assertThat(baseDir.exists(), is(true));

        QavSonarResult result = LogFileParser.analyse(baseDir);

        assertThat(result, notNullValue());
        assertThat(result.getNoErrors(), is(expectedErrors));
        assertThat(result.getNoWarnings(), is(expectedWarnings));
        assertThat(result.isEmpty(), is(expectedEmpty));
    }
}