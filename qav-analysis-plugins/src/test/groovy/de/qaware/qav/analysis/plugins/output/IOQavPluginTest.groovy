package de.qaware.qav.analysis.plugins.output

import de.qaware.qav.analysis.plugins.output.impl.SonarLogUtil
import org.apache.commons.io.FileUtils
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Tests for {@link de.qaware.qav.analysis.plugins.output.IOQavPlugin}. Mostly for the output dir handling.
 *
 * @author QAware GmbH
 */
class IOQavPluginTest {

    private static final String TEST_PATH = "build/ioQavPluginTest/output"

    private File testDir
    private File testFile
    private IOQavPlugin ioQavPlugin

    @Before
    void init() {
        this.ioQavPlugin = new IOQavPlugin()
        testDir = new File(TEST_PATH)
        testFile = new File(testDir, "x")
    }

    @After
    void cleanup() {
        FileUtils.deleteDirectory(testDir)
    }

    @Test
    void testSetOutputDir() {
        assert !testDir.exists()

        ioQavPlugin.setOutputDir(TEST_PATH)

        assert testDir.exists()
        assert testDir.isDirectory()
        assert ioQavPlugin.outputDir == testDir.absolutePath

        File logFile = new File(testDir, SonarLogUtil.LOGFILE_NAME)
        assert logFile.exists()
    }

    @Test
    void testSetOutputDirClears() {
        testDir.mkdirs()

        testFile.write("asdf")
        assert testFile.exists()

        ioQavPlugin.setOutputDir(TEST_PATH, true)
        assert testDir.exists()
        assert testDir.isDirectory()
        assert !testFile.exists()
    }

    @Test
    void testSetOutputDirClearsOnlyIfDemanded() {
        testDir.mkdirs()

        testFile.write("asdf")
        assert testFile.exists()

        ioQavPlugin.setOutputDir(TEST_PATH, false)
        assert testDir.exists()
        assert testDir.isDirectory()
        assert testFile.exists()
    }

    @Test
    public void testSetOutputDirOnlyOnce() {
        String path1 = TEST_PATH;
        String path2 = TEST_PATH + "2";

        ioQavPlugin.setOutputDir(path1)
        File realPath = new File(ioQavPlugin.outputDir)
        assert realPath.canonicalPath == new File(TEST_PATH).canonicalPath

        ioQavPlugin.setOutputDir(path2) // try to change
        realPath = new File(ioQavPlugin.outputDir)
        assert realPath.canonicalPath == new File(TEST_PATH).canonicalPath // remains unchanged!
    }

    @Test(expected = IllegalArgumentException)
    void testSetOutputDirFailsOnWrongDir() {
        testDir.mkdirs()

        testFile.write("asdf")
        assert testFile.exists()
        assert testFile.isFile()

        ioQavPlugin.setOutputDir(testFile.absolutePath, true)
    }

    @Test
    void testDefaultDir() {
        assert ioQavPlugin.outputDir == "./"
    }
}
