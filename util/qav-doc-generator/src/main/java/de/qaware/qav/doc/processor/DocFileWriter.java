package de.qaware.qav.doc.processor;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Writes the generated doc into a file.
 * <p>
 * Creates the output directory (only if required, i.e. only if there actually is a file written).
 *
 * @author QAware GmbH
 */
public class DocFileWriter {

    private final String outputDir;
    private final AnnotationProcessorErrorLogger errorLogger;

    /**
     * Constructor.
     *
     * @param errorLogger the {@link AnnotationProcessorErrorLogger} for error logging
     * @param outputDir   the output directory to use
     */
    public DocFileWriter(AnnotationProcessorErrorLogger errorLogger, String outputDir) {
        this.outputDir = checkNotNull(outputDir, "output directory may not be null");
        this.errorLogger = errorLogger;
    }

    /**
     * Writes the given String to the doc file.
     * <p>
     * Makes sure the output directory exists before writing the file.
     *
     * @param pluginName the plugin name, used to create the file name
     * @param doc        the String to write
     * @throws IllegalStateException when output file can't be written.
     */
    @SuppressWarnings("squid:S1166") // log or rethrow exception. Which is exactly what happens.
    public void writeDocFile(String pluginName, String doc) {
        createOutputDir();
        File outputFile = new File(outputDir, pluginName + ".adoc");
        try {
            Files.write(doc, outputFile, Charsets.UTF_8);
        } catch (IOException e) {
            errorLogger.logError("Can't write to file " + outputFile.getAbsolutePath());
        }
    }

    /**
     * Makes sure the output directory exists.
     * <p>
     * If it fails creating the output directory, writes a message to the error logger.
     */
    private void createOutputDir() {
        File dir = new File(this.outputDir);
        if (!dir.mkdirs() && !dir.exists()) {
            this.errorLogger.logError("Can't create directory '" + this.outputDir + "'");
        }
    }
}
