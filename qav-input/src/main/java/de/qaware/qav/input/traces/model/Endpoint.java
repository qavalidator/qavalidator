package de.qaware.qav.input.traces.model;

import lombok.Data;

/**
 * Model class to read Zipkin Traces from JSON.
 *
 * @author QAware GmbH
 */
@Data
public class Endpoint {

    /**
     * The service called in this Span.
     */
    private String serviceName;

    /**
     * The IP of the service.
     */
    private String ipv4;

    /**
     * The port of the service.
     */
    private Integer port;
}
