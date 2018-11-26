package de.qaware.qav.analysis.plugins.output.impl

import de.qaware.qav.util.FileSystemUtil
import groovy.util.logging.Slf4j
import org.junit.After
import org.junit.Before
import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue

/**
 * Tests for {@link SonarLogUtil}.
 *
 * @author Fabian Huch fabian.huch@qaware.de
 */
@Slf4j
class SonarLogUtilTest {

    private static final String TEST_PATH = "build/SonarLogUtilTest/output"

    private File testDir
    private File testFile


    @Before
    void setUp() throws Exception {
        testDir = new File(TEST_PATH)
        testFile = new File(testDir, SonarLogUtil.LOGFILE_NAME)
        FileSystemUtil.deleteDirectoryQuietly(TEST_PATH)
    }

    @After
    void tearDown() throws Exception {
        FileSystemUtil.deleteDirectoryQuietly(TEST_PATH)
    }

    @Test
    void testSetOutputDir() throws Exception {
        assertFalse(testDir.exists())

        SonarLogUtil.setOutputDir(TEST_PATH)

        assertTrue(testDir.exists())
        assertTrue(testDir.isDirectory())
        assertTrue(testFile.exists())
        assertTrue(testFile.isFile())
    }

    @Test
    void testLogError() throws IOException {
        SonarLogUtil.setOutputDir(TEST_PATH)

        SonarLogUtil.error("INPUT CYCLES 3")
        SonarLogUtil.warn("MAVEN CYCLES 42")

        BufferedReader reader = new BufferedReader(new FileReader(testFile))
        assertThat(reader.readLine()).isEqualTo("ERROR INPUT CYCLES 3")
        assertThat(reader.readLine()).isEqualTo("WARN MAVEN CYCLES 42")
        reader.close()
    }
}