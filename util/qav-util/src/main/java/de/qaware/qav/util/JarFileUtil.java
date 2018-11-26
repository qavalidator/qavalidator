package de.qaware.qav.util;

import com.google.common.io.ByteStreams;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Work with JAR files: Find class files and nested JAR files.
 * <p>
 * This class writes nested JAR files as temporary files, as the {@link JarFile} class does not support nested jars.
 * Deletes them after reading. Therefore, we need access to the "temp" directory, but this class will clean up and
 * not leave any files on disk.
 * <p>
 * Alternatives like the Spring Boot JarFile class can't work with compressed nested JARs.
 *
 * @author QAware GmbH
 */
@Slf4j
public final class JarFileUtil {

    /**
     * util class, no instances.
     */
    private JarFileUtil() {
    }

    /**
     * Reads the given file as JAR file.
     * <p>
     * Handles all entries that match the given parameters map, i.e. at least one "includes" pattern and no "exclude"
     * patterns must match. Recursively goes down all *.jar, *.war, *.ear files.
     *
     * @param base         the file; must not be <tt>null</tt>
     * @param parameters   the parameters map. Has one entry for "includes" and one for "excludes" patterns.
     * @param classHandler the {@link ClassHandler} which will handle the class files.
     */
    @SuppressWarnings("squid:S1166") // wants log or rethrow exception. It's logged well enough here.
    public static void readJarFile(File base, Map parameters, ClassHandler classHandler) {
        checkNotNull(base, "Jar file must be given");
        LOGGER.info("Reading JAR file: {}", base.getAbsolutePath());

        try {
            readJarFile(new JarFile(base), parameters, classHandler);
        } catch (IOException e) {
            LOGGER.error("Error reading jar file: {}: {}", base.getAbsolutePath(), e.getMessage());
        }
    }

    /**
     * Actually reads the JAR file.
     *
     * @param jarFile      the {@link JarFile}
     * @param parameters   the parameters map. Has one entry for "includes" and one for "excludes" patterns.
     * @param classHandler the {@link ClassHandler} which will handle the class files.
     * @throws IOException if any IO doesn't work: writing or deleting temp dirs or files, or opening the nested jars.
     */
    private static void readJarFile(JarFile jarFile, Map parameters, ClassHandler classHandler) throws IOException {
        File tempDirectory = Files.createTempDirectory(null).toFile();
        LOGGER.debug("Created temp dir: {}", tempDirectory.getAbsolutePath());

        int noNestedJars = 0;
        int noClasses = 0;
        for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements(); ) {
            JarEntry entry = entries.nextElement();
            LOGGER.debug("Entry: {}", entry.getName());

            if (FileNameUtil.isIncluded(entry.getName(), parameters)) {
                if (isJarEntry(entry.getName())) {
                    // write temp file and recurse
                    File tempFile = writeNestedJarToTempFile(jarFile, entry, tempDirectory);
                    JarFile nestedJarFile = new JarFile(tempFile);
                    readJarFile(nestedJarFile, parameters, classHandler);
                    nestedJarFile.close();
                    delete(tempFile);
                    noNestedJars++;
                } else {
                    // read bytes and do analysis
                    InputStream inputStream = jarFile.getInputStream(entry);
                    byte[] bytes = ByteStreams.toByteArray(inputStream);
                    classHandler.handleClass(entry.getName(), bytes);
                    noClasses++;
                }
            }
        }

        LOGGER.info("Read {}: {} classes and {} jar files", jarFile.getName(), noClasses, noNestedJars);
        delete(tempDirectory);
    }

    /**
     * returns true if the file can be handled like a JAR file. This is the case for *.jar, *.war, *.ear files
     * (hardcoded).
     *
     * @param name name of the file
     * @return <tt>true</tt> if it's a JAR-like file, <tt>false</tt> if not.
     */
    public static boolean isJarEntry(String name) {
        return name.endsWith(".jar")
                || name.endsWith(".war")
                || name.endsWith(".ear");
    }

    private static File writeNestedJarToTempFile(JarFile jarFile, JarEntry entry, File tempDirectory) throws IOException {
        InputStream inputStream = jarFile.getInputStream(entry);
        byte[] entryAsBytes = ByteStreams.toByteArray(inputStream);
        File tempFile = new File(tempDirectory, entry.getName());
        com.google.common.io.Files.write(entryAsBytes, tempFile);
        LOGGER.debug("Created temp file: {}", tempFile.getAbsolutePath());
        return tempFile;
    }

    private static void delete(File tempFile) {
        try {
            Files.delete(tempFile.toPath());
            LOGGER.debug("Deleted: {}", tempFile.getAbsolutePath());
        } catch(IOException e) {
            LOGGER.error("Could not delete: {}", tempFile.getAbsolutePath(), e);
        }
    }

}
