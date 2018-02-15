package de.qaware.qav.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Util methods for dealing with files and directories,
 * and with resources from classpath.
 *
 * @author QAware GmbH
 */
public final class FileSystemUtil {

    public static final String CLASSPATH_PREFIX = "classpath:";

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemUtil.class);

    /**
     * util class with only static methods.
     */
    private FileSystemUtil() {
    }

    /**
     * checks if the given filename exists.
     *
     * First checks if there is a file, then checks if there is a resource on the classpath, then checks if the
     * filename starts with "classpath:/", strips of that prefix and checks if there is such a resource.
     *
     * @param filename the filename to check
     * @return true if the file exists
     */
    public static boolean checkFileOrResourceExists(String filename) {
        if (new File(filename).exists()) {
            return true;
        }

        InputStream is = FileSystemUtil.class.getResourceAsStream(filename);
        if (is != null) {
            IOUtils.closeQuietly(is);
            return true;
        }

        if (filename.startsWith(CLASSPATH_PREFIX)) {
            is = FileSystemUtil.class.getResourceAsStream(filename.substring(CLASSPATH_PREFIX.length()));
            if (is != null) {
                IOUtils.closeQuietly(is);
                return true;
            }
        }

        return false;
    }

    /**
     * Read string from resource or file.
     *
     * @param filename if starts with "classpath:/", try as resource. If not, try to find file.
     * @return the content of the resource or file as String.
     */
    public static String readFileAsText(String filename) {
        InputStream inStream = null;

        try {
            if (filename.startsWith(CLASSPATH_PREFIX)) {
                inStream = openResourceStream(filename.substring(CLASSPATH_PREFIX.length()));
            } else {
                inStream = openFileStream(filename);
            }

            String result = IOUtils.toString(inStream, Charset.defaultCharset());
            if (StringUtils.isEmpty(result)) {
                throw new IllegalArgumentException("File '" + filename + "' is empty.");
            }
            return result;
        } catch (IOException e) {
            throw new IllegalStateException("Error reading file '" + filename + "': " + e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(inStream);
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
        try {
            return readFileAsText(fileName);
        } catch (IllegalArgumentException e) {
            if (!StringUtils.isEmpty(alternateDir)) {
                return readFileAsText(alternateDir + File.separator + fileName);
            } else {
                throw e;
            }
        }
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
        try {
            FileUtils.writeStringToFile(outputFile, content, Charset.defaultCharset(), append);
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
            return FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            String msg = String.format("Error reading file %s: %s", file.getAbsolutePath(), e.getMessage());
            LOGGER.error(msg);
            throw new IllegalArgumentException(msg, e);
        }
    }

}
