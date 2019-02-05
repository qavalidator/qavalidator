package de.qaware.qav.app.server.controller;

import java.util.regex.Pattern;

/**
 * Check an sanitize filenames given as input.
 */
public final class InputSanitizer {

    private static final Pattern VALID_LETTERS = Pattern.compile("[a-zA-Z0-9./\\-_]++");
    private static final Pattern INVALID_PARTS = Pattern.compile(".*\\.\\..*");

    /** util class, no instance. */
    private InputSanitizer() {
    }

    /**
     * Validate the input param for the filename.
     *
     * @param filename the filename
     * @return the sanitized name
     * @throws IllegalArgumentException if the filename violates a naming convention
     */
    public static String validateImageName(String filename) {
        if (!VALID_LETTERS.matcher(filename).matches()
                || INVALID_PARTS.matcher(filename).matches()) {
            throw new IllegalArgumentException("invalid image name: " + filename);
        }
        return filename;
    }
}
