package de.qaware.qav.server.model;

import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Map;

/**
 * DTO with all Node details for display at the UI.
 *
 * @author QAware GmbH
 */
@Data
@ToString
public class NodeDTO {

    private String name;

    private Map<String, Object> properties;

    private List<DependencyDTO> parents;
    private List<DependencyDTO> incomingDeps;
    private List<DependencyDTO> outgoingDeps;
    private List<DependencyDTO> containedDeps;
}
