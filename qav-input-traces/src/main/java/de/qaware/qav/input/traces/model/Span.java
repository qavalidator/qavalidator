package de.qaware.qav.input.traces.model;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

/**
 * Model class to read Zipkin Traces from JSON.
 *
 * @author QAware GmbH
 */
@Data
public class Span {

    public static final int NANOS_PER_MILLI = 1000;
    /**
     * The Zipkin TraceID.
     */
    private String traceId;

    /**
     * ID of the span.
     */
    private String id;

    /**
     * Name of the service.
     */
    private String name;

    /**
     * The name of the caller.
     * This is null if we don't know the caller. Normal for calls from outside; a bug in any other situation.
     */
    private String parentId;

    @SuppressWarnings("squid:S1450")
    // due to the setter we introduced for JSON, Sonar wants this field to be moved to the setter method
    private LocalDateTime timestamp;
    private Long duration;

    // further infos on te Span:
    private List<BinaryAnnotation> annotations;
    private List<BinaryAnnotation> binaryAnnotations;

    private Boolean debug;

    /**
     * Setter, for reading the JSON file.
     *
     * @param ts the time in nanos (!) -- we'll divide be 1000 to get millis and construct the {@link LocalDateTime}
     *           from it
     */
    public void setTimestamp(long ts) {
        this.timestamp = LocalDateTime.ofInstant(Instant.ofEpochMilli(ts / NANOS_PER_MILLI), ZoneId.systemDefault());
    }
}
