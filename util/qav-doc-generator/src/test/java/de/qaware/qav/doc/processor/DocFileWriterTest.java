package de.qaware.qav.doc.processor;

import com.google.common.io.Files;
import de.qaware.qav.util.FileSystemUtil;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Tests for {@link DocFileWriter}.
 *
 * @author QAware GmbH
 */
public class DocFileWriterTest {

    private static final Logger LOGGER = getLogger(DocFileWriterTest.class);

    private AnnotationProcessorErrorLogger errorLogger = LOGGER::error;
    private String outputDirName;
    private File outputDir;

    @Before
    public void setup() {
        outputDirName = "build/tmp/testDir";
        outputDir = new File(outputDirName);

        FileSystemUtil.deleteDirectoryQuietly(outputDirName);
        assertThat(outputDir.exists(), is(false));
    }

    @Test
    public void testWriteDocFile() throws IOException {
        DocFileWriter docFileWriter = new DocFileWriter(errorLogger, outputDirName);
        assertThat(outputDir.exists(), is(false));
        docFileWriter.writeDocFile("MyPlugin", "mydoc\nand so on");
        assertThat(outputDir.exists(), is(true));

        File file = new File(this.outputDirName, "MyPlugin.adoc");
        assertThat(file.exists(), is(true));
        List<String> lines = Files.readLines(file, Charset.defaultCharset());
        assertThat(lines.size(), is(2));
        assertThat(lines.get(0), is("mydoc"));
        assertThat(lines.get(1), is("and so on"));
    }

    @Test(expected = NullPointerException.class)
    public void testWriteDocFileWrongDirectory() {
        new DocFileWriter(errorLogger, null);
    }
}