package de.qaware.qav.doc.processor;

/**
 * Error logger for Java Annotation Processing.
 *
 * @author QAware GmbH
 */
public interface AnnotationProcessorErrorLogger {

    /**
     * Logs an error message.
     *
     * @param message the message
     */
    void logError(String message);

}
