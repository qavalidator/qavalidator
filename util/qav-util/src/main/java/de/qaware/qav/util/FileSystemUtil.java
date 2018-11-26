package de.qaware.qav.util;

import com.google.common.io.CharStreams;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Comparator;

/**
 * Util methods for dealing with files and directories, and with resources from classpath.
 *
 * @author QAware GmbH
 */
@Slf4j
public final class FileSystemUtil {

    public static final String CLASSPATH_PREFIX = "classpath:";

    /**
     * Util class with only static methods.
     */
    private FileSystemUtil() {
    }

    /**
     * Checks if the given filename exists.
     * <p>
     * First checks if there is a file, then checks if there is a resource on the classpath; if the filename starts with
     * "classpath:/", strips off that prefix before checking.
     *
     * @param filename the filename to check
     * @return true if the file exists
     */
    public static boolean checkFileOrResourceExists(String filename) {
        if (new File(filename).exists()) {
            return true;
        }

        try (InputStream is = FileSystemUtil.class.getResourceAsStream(removeClasspathPrefix(filename))) {
            return is != null;
        } catch (IOException e) {
            LOGGER.error("Error reading resource {}", filename);
            return false;
        }
    }

    /**
     * Read string from resource or file.
     *
     * @param filename if starts with "classpath:/", try as resource. If not, try to find file.
     * @return the content of the resource or file as String.
     */
    public static String readFileAsText(String filename) {
        try (InputStream is = openStream(filename)) {
            String result = CharStreams.toString(new InputStreamReader(is, Charset.defaultCharset()));
            if (StringUtils.isEmpty(result)) {
                throw new IllegalArgumentException("File '" + filename + "' is empty.");
            }
            return result;
        } catch (IOException e) {
            throw new IllegalStateException("Error reading file '" + filename + "': " + e.getMessage(), e);
        }
    }

    /**
     * Reads a file from the given file name. If the name starts with "classpath:/", it's read as a resource from
     * classpath.
     *
     * @param fileName     the file name to read
     * @param alternateDir another directory to look for the file if it isn't found in the first place
     * @return the content of the file as a String
     */
    public static String readFileAsText(String fileName, String alternateDir) {
        if (checkFileOrResourceExists(fileName)) {
            return readFileAsText(fileName);
        }

        if (!StringUtils.isEmpty(alternateDir)) {
            return readFileAsText(alternateDir + File.separator + fileName);
        } else {
            throw new IllegalArgumentException("File or resource not found: " + fileName);
        }
    }

    /**
     * Open the input stream either from resource of from file.
     *
     * @param filename the filename
     * @return the resource or file as {@link InputStream}
     */
    private static InputStream openStream(String filename) {
        InputStream inStream;
        if (filename.startsWith(CLASSPATH_PREFIX)) {
            inStream = openResourceStream(removeClasspathPrefix(filename));
        } else {
            inStream = openFileStream(filename);
        }
        return inStream;
    }

    /**
     * opens the given resource from classpath.
     *
     * @param resourceName the name of the resource
     * @return the {@link InputStream}
     * @throws IllegalArgumentException if the resource can't be found
     */
    private static InputStream openResourceStream(String resourceName) {
        InputStream result = FileSystemUtil.class.getResourceAsStream(resourceName);
        if (result == null) {
            throw new IllegalArgumentException("Resource '" + resourceName + "' not found");
        }
        return result;
    }

    /**
     * opens the given file as input stream
     *
     * @param filename the file name
     * @return the {@link InputStream}
     * @throws IllegalArgumentException if the file doesn't exist
     * @throws IllegalStateException    if the file can't be found, but seemed to exist
     */
    private static InputStream openFileStream(String filename) {
        try {
            return new FileInputStream(filename);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("File '" + filename + "' not found", e);
        }
    }

    /**
     * If the given filename starts with the prefix {@link #CLASSPATH_PREFIX}, then the method removes the prefix from
     * the given filename.
     *
     * @param filename the filename
     * @return the filename without the prefix {@link #CLASSPATH_PREFIX}
     */
    private static String removeClasspathPrefix(String filename) {
        if (filename.startsWith(CLASSPATH_PREFIX)) {
            return filename.substring(CLASSPATH_PREFIX.length());
        } else {
            return filename;
        }
    }


    /**
     * Writes the given String into the file. Swallows IOExceptions, just logs an error.
     *
     * @param content  the String to write
     * @param filename the file to write to
     */
    public static void writeStringToFile(String content, String filename) {
        writeStringToFile(content, filename, false);
    }

    /**
     * Writes the given String into the file. Swallows IOExceptions, just logs an error.
     *
     * @param content  the String to write
     * @param filename the file to write to
     * @param append   <tt>true</tt> to append, <tt>false</tt> to overwrite
     */
    @SuppressWarnings("squid:S1166") // wants log or rethrow exception. It's logged well enough here.
    public static void writeStringToFile(String content, String filename, boolean append) {
        File outputFile = new File(filename);
        outputFile.getParentFile().mkdirs();
        try {
            if (append) {
                Files.asCharSink(outputFile, Charset.defaultCharset(), FileWriteMode.APPEND).write(content);
            } else {
                Files.asCharSink(outputFile, Charset.defaultCharset()).write(content);
            }
        } catch (IOException e) {
            LOGGER.error("Could not write on file '{}': {}", outputFile.getAbsolutePath(), e.getMessage());
        }
    }

    /**
     * reads the raw bytes from the given file.
     *
     * @param filename the file name
     * @return the byte array. <tt>null</tt> if the file can not be read.
     */
    @SuppressWarnings("squid:S1166") // wants log or rethrow exception. It's logged well enough here.
    public static byte[] readBytesFromFile(String filename) {
        File file = new File(filename);
        try {
            return Files.toByteArray(file);
        } catch (IOException e) {
            String msg = String.format("Error reading file %s: %s", file.getAbsolutePath(), e.getMessage());
            LOGGER.error(msg);
            throw new IllegalArgumentException(msg, e);
        }
    }

    /**
     * Delete the given directory recursively.
     *
     * @param rootDirName the root directory name
     */
    public static void deleteDirectoryQuietly(String rootDirName) {
        File rootDir = new File(rootDirName);
        if (rootDir.exists()) {
            try {
                java.nio.file.Files.walk(rootDir.toPath())
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            } catch (IOException e) {
                LOGGER.error("Can't delete directory {}: ", rootDirName, e);
            }
        }
    }

}
