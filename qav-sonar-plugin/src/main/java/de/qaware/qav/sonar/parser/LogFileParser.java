package de.qaware.qav.sonar.parser;

import com.google.common.io.Files;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Finds all QAvalidator Sonarqube logfiles,
 * and parses Warnings and Errors.
 * <p>
 * See also de.qaware.qav.core.sonar.SonarLogUtil for constant's definitions.
 * They are repeated here to avoid a compile time dependency to the QAvalidator core.
 *
 * @author QAware GmbH
 */
public final class LogFileParser {

    private static final Logger LOGGER = getLogger(LogFileParser.class);

    public static final String LOGFILE_NAME = "qav-sonar.log";
    public static final String ERROR_PREFIX = "ERROR ";
    public static final String WARNING_PREFIX = "WARN ";

    /**
     * util class, no instance.
     */
    private LogFileParser() {
    }

    /**
     * Do the analysis, i.e.:
     * <p>
     * identify relevant files,
     * parse them for ERROR / WARN prefixes, and
     * count those lines.
     *
     * @param baseDir teh base dir to start looking for QAvalidator Sonar log files
     * @return the {@link QavSonarResult} with number of warnings and errors
     */
    public static QavSonarResult analyse(File baseDir) {
        List<File> files = LogFileFinder.findResultFiles(baseDir);
        QavSonarResult result = new QavSonarResult();
        result.setEmpty(files.isEmpty());
        for (File file : files) {
            LogFileParser.analyseFile(result, file);
        }
        return result;
    }

    /**
     * Analyse one file.
     *
     * @param result     the {@link QavSonarResult} to sum the result
     * @param resultFile the file to parse
     */
    @SuppressWarnings("squid:S1166") // wants log or rethrow exception. It's logged well enough here.
    private static void analyseFile(QavSonarResult result, File resultFile) {
        LOGGER.info("Reading file: {}", resultFile.getAbsolutePath());
        try {
            List<String> lines = Files.readLines(resultFile, StandardCharsets.UTF_8);
            countWarningAndErrors(result, lines);
        } catch (IOException e) {
            LOGGER.error("Can't read file {}: {}", resultFile.getAbsolutePath(), e.getMessage());
        }
    }

    /**
     * Counts WARN / ERROR lines.
     *
     * @param result the {@link QavSonarResult} to sum the result
     * @param lines  the content of the file, line by line.
     */
    private static void countWarningAndErrors(QavSonarResult result, List<String> lines) {
        for (String line : lines) {
            if (line != null) {
                if (line.startsWith(ERROR_PREFIX)) {
                    result.incErrors();
                } else if (line.startsWith(WARNING_PREFIX)) {
                    result.incWarnings();
                } else {
                    LOGGER.info("Wrong input: line does not start with ERROR or WARN: {}", line);
                    result.incErrors();
                }
            }
        }
    }
}
