package de.qaware.qav.doc.processor;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.CompilationSubject;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.hamcrest.Matchers.is;

/**
 * Tests for {@link DocProcessor}.
 *
 * @author QAware GmbH
 */
public class DocProcessorTest {

    private static final String EXPECTED_DIR = "src/test/resources";
    private static final String GENERATED_DIR = "build/src-gen/generated-docs";

    private DocProcessor docProcessor;

    @Before
    public void setup() {
        docProcessor = new DocProcessor();
        docProcessor.setOutputDir(GENERATED_DIR);
    }

    @Test
    public void testDocProcessorOnSimpleInput() throws IOException {
        testAnnotationProcessor("TestPlugin.java", "TestPlugin.adoc", "TestPlugin_expected.adoc");
    }

    @Test
    public void testDocProcessorOnStandardInput() throws IOException {
        testAnnotationProcessor("TestPlugin2.java", "TestPlugin2.adoc", "TestPlugin2_expected.adoc");
    }

    protected void testAnnotationProcessor(String className, String outputName, String expectedOutputFile) throws IOException {
        Compilation compilation = Compiler.javac()
                .withProcessors(docProcessor)
                .compile(JavaFileObjects.forResource(className));

        CompilationSubject.assertThat(compilation).succeeded();

        File resultFile = new File(GENERATED_DIR, outputName);
        Assert.assertThat(resultFile.exists(), is(true));

        List<String> resultLines = Files.readAllLines(resultFile.toPath());

        File expectedFile = new File(EXPECTED_DIR, expectedOutputFile);
        Assert.assertThat(expectedFile.exists(), is(true));
        List<String> expectedLines = Files.readAllLines(expectedFile.toPath());

        Assert.assertThat(resultLines.toString(), is(expectedLines.toString()));
    }

}