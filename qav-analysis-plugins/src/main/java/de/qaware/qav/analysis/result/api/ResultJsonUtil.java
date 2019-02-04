package de.qaware.qav.analysis.result.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Util class to create the JSON mapper.
 */
public final class ResultJsonUtil {

    /** util class, no instances. */
    private ResultJsonUtil() {
    }

    /**
     * Initialize the Jackson JSON mapper.
     *
     * Configured to indent the output, and to use a readable timestamp string format.
     *
     * @return the configured {@link ObjectMapper}
     */
    public static ObjectMapper initJsonMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .enable(SerializationFeature.INDENT_OUTPUT)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

}
