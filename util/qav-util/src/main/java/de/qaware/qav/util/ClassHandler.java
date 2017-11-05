package de.qaware.qav.util;

/**
 * Handle classes, i.e. pass them on to analysis.
 *
 * @author QAware GmbH
 */
public interface ClassHandler {

    /**
     * Handle class, i.e. pass it on to analysis.
     *
     * @param name    name of the file from where the class was read
     * @param content the class itself, as byte array
     */
    void handleClass(String name, byte[] content);
}
