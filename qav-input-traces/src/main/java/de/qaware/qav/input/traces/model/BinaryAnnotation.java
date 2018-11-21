package de.qaware.qav.input.traces.model;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Model class to read Zipkin Traces from JSON.
 *
 * @author QAware GmbH
 */
@Data
public class BinaryAnnotation {

    private LocalDateTime timestamp;

    /**
     * key/value: for generic key/value data
     */
    private String key;
    private String value;

    /**
     * The callee of the span
     */
    private Endpoint endpoint;

    /**
     * Setter, for reading the JSON file.
     *
     * @param ts the time in nanos (!) -- we'll divide be 1000 to get millis and construct the {@link LocalDateTime}
     *           from it
     */
    public void setTimestamp(long ts) {
        this.timestamp = LocalDateTime.ofInstant(Instant.ofEpochMilli(ts / 1000), ZoneId.systemDefault());
    }
}
