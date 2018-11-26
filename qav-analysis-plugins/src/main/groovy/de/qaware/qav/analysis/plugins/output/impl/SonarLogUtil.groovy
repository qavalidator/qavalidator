package de.qaware.qav.analysis.plugins.output.impl

import de.qaware.qav.util.FileSystemUtil

import static com.google.common.base.Preconditions.checkNotNull

/**
 * Write Log output which is meant for Sonar analysis.
 * <p>
 * This is a workaround for the use of QAvalidator from a Maven plugin; we'd rather use Log4j, but Maven changes the
 * log4j settings.
 *
 * @author Fabian Huch fabian.huch@qaware.de
 */
final class SonarLogUtil {

    public static final String LOGFILE_NAME = "qav-sonar.log"
    public static final String ERROR_PREFIX = "ERROR "
    public static final String WARNING_PREFIX = "WARN "

    private static File logFile = new File(".", LOGFILE_NAME)

    /**
     * Util class, no instances.
     */
    private SonarLogUtil() {
    }

    /**
     * Creates a new empty log file.
     *
     * @param outputDir directory where to put the output file
     */
    static void setOutputDir(String outputDir) {
        checkNotNull(outputDir, "outputDir")

        logFile = new File(outputDir, LOGFILE_NAME)

        // truncate the log file:
        def parentDir = logFile.parentFile
        parentDir.mkdirs()
        FileSystemUtil.writeStringToFile("", logFile.getAbsolutePath())
    }

    /**
     * Writes an error message.
     *
     * @param message the message
     */
    static void error(String message) {
        logMessage(ERROR_PREFIX + message)
    }

    /**
     * Writes a warning message.
     *
     * @param message the message
     */
    static void warn(String message) {
        logMessage(WARNING_PREFIX + message)
    }

    /**
     * Logs a message to the sonar log file
     *
     * @param message the message to log
     */
    private static void logMessage(String message) {
        checkNotNull(message, "message")
        FileSystemUtil.appendStringToFile(message + "\n", logFile.getAbsolutePath())
    }
}
