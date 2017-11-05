package de.qaware.qav.sonar.parser;

import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

/**
 * Tests for {@link LogFileFinder}.
 *
 * @author QAware GmbH
 */
public class LogFileFinderTest {

    @Test
    public void findResultFiles() {
        checkDirectory("src/test/resources/logTest1", 1);
        checkDirectory("src/test/resources/logTest2", 2);
        checkDirectory("src/test/resources/logTest3", 0);
        checkDirectory("src/test/resources/logTest4", 1);
        checkDirectory("src/test/resources/logTest5", 1);

        // also find log files deeper down the hierarchy:
        checkDirectory("src/test/resources", 5);
    }

    private void checkDirectory(String baseDirName, int numExpectedFiles) {
        File baseDir = new File(baseDirName);
        assertThat(baseDir.exists(), is(true));

        List<File> resultFiles = LogFileFinder.findResultFiles(baseDir);

        assertThat(resultFiles, hasSize(numExpectedFiles));
    }
}
