package de.qaware.qav.util;

import com.google.common.io.Files;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests for {@link FileSystemUtil}.
 *
 * @author QAware GmbH
 */
public class FileSystemUtilTest {

    @Test
    public void testCheckFileExists() {
        assertThat(FileSystemUtil.checkFileOrResourceExists("build.gradle"), is(true));
        assertThat(FileSystemUtil.checkFileOrResourceExists("/stg/Test.stg"), is(true));
        assertThat(FileSystemUtil.checkFileOrResourceExists("classpath:/stg/Test.stg"), is(true));
        assertThat(FileSystemUtil.checkFileOrResourceExists("/not/existing/file"), is(false));
        assertThat(FileSystemUtil.checkFileOrResourceExists("classpath:/not/existing/resource"), is(false));
    }

    @Test
    public void readFileAsTextFromResource() {
        String result = FileSystemUtil.readFileAsText("classpath:/stg/Test.stg");
        assertThat(result, containsString("group TEST"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void readFileAsTextFromResourceNotFound() {
        FileSystemUtil.readFileAsText("classpath:/not/existing/resource.txt");
    }

    @Test
    public void readFileAsTextFromFile() {
        String result = FileSystemUtil.readFileAsText("src/test/resources/stg/Test.stg");
        assertThat(result, containsString("group TEST"));
    }

    @Test
    public void readFileAsTextFromResourceAtAlternateLocation() {
        String result = FileSystemUtil.readFileAsText("Test.stg", "classpath:/stg");
        assertThat(result, containsString("group TEST"));
    }

    @Test
    public void readFileAsTextFromFileAtAlternateLocation() {
        String result = FileSystemUtil.readFileAsText("Test.stg", "src/test/resources/stg");
        assertThat(result, containsString("group TEST"));
    }

    @Test
    public void readFileAsTextFromFileAtAlternateLocationButFindAtFirst() {
        String result = FileSystemUtil.readFileAsText("src/test/resources/stg/Test.stg", "src/test/resources/stg");
        assertThat(result, containsString("group TEST"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void readFileAsTextNotFound() {
        FileSystemUtil.readFileAsText("/not/existing/file.txt");
    }

    @Test(expected = IllegalArgumentException.class)
    public void readFileAsTextNotFoundFromAlternateLocation() {
        FileSystemUtil.readFileAsText("/not/existing/file.txt", "/also/not/here");
    }

    @Test(expected = IllegalArgumentException.class)
    public void readFileAsTextNotFoundWithEmptAlternateLocation() {
        FileSystemUtil.readFileAsText("/not/existing/file.txt", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void readFileAsTextEmpty() {
        FileSystemUtil.readFileAsText("classpath:/empty_file.txt", "");
    }

    @Test
    public void writeEmptyStringToFile() {
        String testString = "";
        String filename = "build/test_file1.txt";
        FileSystemUtil.writeStringToFile(testString, filename);
        assertTrue(new File(filename).exists());
        try {
            FileSystemUtil.readFileAsText(filename);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("File 'build/test_file1.txt' is empty."));
        }
    }

    @Test
    public void testWriteStringToFile() {
        String testString = "Hallo hallo\nHallo hallo";
        String filename = "build/test_file2.txt";
        FileSystemUtil.writeStringToFile(testString, filename);
        String result = FileSystemUtil.readFileAsText(filename);
        assertThat(result, is(testString));
    }

    @Test
    public void testWriteStringToFileAppend() {
        String testString = "Hallo hallo\n";
        String filename = "build/test_file.txt";
        FileSystemUtil.writeStringToFile(testString, filename);
        FileSystemUtil.appendStringToFile(testString, filename);
        FileSystemUtil.appendStringToFile(testString, filename);

        String result = FileSystemUtil.readFileAsText(filename);
        assertThat(result, is(testString + testString + testString));
    }

    @Test
    public void testReadBytesFromFile() throws IOException {
        String filename = "build/bytes_test_file";
        byte[] bytes = {1, 2, 3, 10, 11, 12};
        Files.write(bytes, new File(filename));
        byte[] result = FileSystemUtil.readBytesFromFile(filename);
        assertNotNull(result);
        assertThat(result, is(bytes));
    }

    @Test
    public void testReadBytesFromFileNotExisting() {
        String filename = "build/not_existing_file";
        try {
            FileSystemUtil.readBytesFromFile(filename);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage().startsWith("Error reading file "), is(true));
        }
    }

    @Test
    public void deleteDirectoryQuietlyWhenNotExistent() {
        String dirname = "build/d-test";
        File dir = new File(dirname);
        assertFalse(dir.exists());
        FileSystemUtil.deleteDirectoryQuietly(dirname);
        assertFalse(dir.exists());
    }

    @Test
    public void deleteDirectoryQuietly() {
        String dirname = "build/d-test";
        File dir = new File(dirname);
        assertFalse(dir.exists());
        dir.mkdirs();
        assertTrue(dir.exists());

        FileSystemUtil.deleteDirectoryQuietly(dirname);
        assertFalse(dir.exists());
    }
}