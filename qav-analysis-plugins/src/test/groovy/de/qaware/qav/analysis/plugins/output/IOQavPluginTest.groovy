package de.qaware.qav.analysis.plugins.output

import de.qaware.qav.analysis.plugins.output.impl.SonarLogUtil
import de.qaware.qav.analysis.plugins.test.TestAnalysis
import de.qaware.qav.architecture.dsl.model.Architecture
import de.qaware.qav.graph.api.DependencyGraph
import de.qaware.qav.graph.api.DependencyType
import de.qaware.qav.graph.factory.DependencyGraphFactory
import de.qaware.qav.util.FileSystemUtil
import de.qaware.qav.visualization.Abbreviation
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
        FileSystemUtil.deleteDirectoryQuietly(TEST_PATH)
    }

    @Test
    void testApply() {
        def analysis = new TestAnalysis()
        ioQavPlugin.apply(analysis)
        assert analysis.closureMap.size() == 7
    }

    @Test
    void testAbbreviations() {
        assert ioQavPlugin.abbreviations.isEmpty()
        ioQavPlugin.registerAbbreviation("Long name", "L")
        ioQavPlugin.registerAbbreviation("Long name 2", "L2")
        assert ioQavPlugin.abbreviations.size() == 2
        assert ioQavPlugin.abbreviations == [
                new Abbreviation("Long name", "L"),
                new Abbreviation("Long name 2", "L2")
        ]
    }

    @Test
    void testPrintEmptyGraph() {
        ioQavPlugin.setOutputDir(TEST_PATH, true)
        File outputFile = new File(testDir, "testGraph.txt")

        DependencyGraph graph = DependencyGraphFactory.createGraph()

        // don't print empty graph:
        ioQavPlugin.printNodes(graph, "testGraph.txt", false)
        assert !outputFile.exists()

        // do print empty graph. This also proves that the path used in the test above is correct
        ioQavPlugin.printNodes(graph, "testGraph.txt", true)
        assert outputFile.exists()

        // test the default setting:
        FileSystemUtil.deleteDirectoryQuietly(TEST_PATH)
        testDir.mkdirs()
        ioQavPlugin.printNodes(graph, "testGraph.txt")
        assert outputFile.exists()
    }

    @Test
    void testPrintGraph() {
        ioQavPlugin.setOutputDir(TEST_PATH, true)
        File outputFile = new File(testDir, "testGraph.txt")

        DependencyGraph graph = createSampleGraph()

        ioQavPlugin.printNodes(graph, "testGraph.txt")
        assert outputFile.exists()

        File expectedFile = new File("src/test/resources/testGraphExpected.txt")
        assert expectedFile.exists()
        String expected = normalizeText(expectedFile)
        String actual = normalizeText(outputFile)

        assert expected == actual
    }

    @Test
    void testWriteFile() {
        DependencyGraph graph = createSampleGraph()
        ioQavPlugin.setOutputDir(TEST_PATH, true)
        File outputFile = new File(testDir, "testGraph.json")
        assert !outputFile.exists()

        ioQavPlugin.writeFile(graph, "testGraph.json")
        assert outputFile.exists()

        File expectedFile = new File("src/test/resources/testGraphExpected.json")
        assert expectedFile.exists()
        String expected = normalizeText(expectedFile)
        String actual = normalizeText(outputFile)

        assert expected == actual
    }

    @Test
    void testWriteDot() {
        DependencyGraph graph = createSampleGraph()
        ioQavPlugin.setOutputDir(TEST_PATH, true)
        File outputFile = new File(testDir, "testGraph.dot")
        assert !outputFile.exists()

        ioQavPlugin.writeDot(graph, "testGraph", new Architecture())
        assert outputFile.exists()

        File expectedFile = new File("src/test/resources/testGraphExpected.dot")
        assert expectedFile.exists()
        String expected = normalizeText(expectedFile)
        String actual = normalizeText(outputFile)

        assert expected == actual
    }

    static private DependencyGraph createSampleGraph() {
        DependencyGraph graph = DependencyGraphFactory.createGraph()
        def v1 = graph.getOrCreateNodeByName("v1")
        def v2 = graph.getOrCreateNodeByName("v2")
        graph.addDependency(v1, v2, DependencyType.READ_ONLY)
        graph
    }

    static private String normalizeText(File expectedFile) {
        expectedFile.text
                .replaceAll("\r", "\n")
                .replaceAll("\n\n", "\n")
                .replaceAll("\n\n", "\n")
                .trim()
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
    void testSetOutputDirOnlyOnce() {
        String path1 = TEST_PATH
        String path2 = TEST_PATH + "2"

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

    @Test
    void testWriteLegend() {
        ioQavPlugin.setOutputDir("build/test-output/")
        ioQavPlugin.writeGraphLegend("legend")

        File legendFile = new File("build/test-output", "legend.dot")
        assert legendFile.exists()
    }
}
