package de.qaware.qav.sonar.parser;

import org.apache.tools.ant.DirectoryScanner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Finds QAvalidator Sonar log files.
 *
 * There may be more than one, so we find and analyze all of them.
 *
 * @author QAware GmbH
 */
public final class LogFileFinder {

    /**
     * util class without instances
     */
    private LogFileFinder() {
    }

    /**
     * Finds all files with the name {@link LogFileParser#LOGFILE_NAME} under the given baseDir.
     *
     * Excludes directories named "test" in order to avoid errors and warnings from this plugin's own unit tests.
     *
     * @param baseDir the directory under which to search for {@link LogFileParser#LOGFILE_NAME} files.
     * @return the list of files. May be empty, but never null.
     */
    public static List<File> findResultFiles(File baseDir) {
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(baseDir);
        scanner.setIncludes(new String[]{"**/" + LogFileParser.LOGFILE_NAME});
        // exclude files used for testing, in src/test/resources
        scanner.setExcludes(new String[]{"**/test/"});
        scanner.scan();

        String[] files = scanner.getIncludedFiles();

        List<File> result = new ArrayList<>();
        for (String filename : files) {
            result.add(new File(baseDir, filename));
        }

        return result;
    }
}
