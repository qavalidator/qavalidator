package de.qaware.qav.app.server.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * DTO with all Node details for display at the UI.
 *
 * @author QAware GmbH
 */
@Data
public class NodeDTO {

    /**
     * Name of the node.
     */
    private String name;

    /**
     * Properties of the node.
     */
    private Map<String, Object> properties;

    /**
     * There may be more parents, if the node is part of several architecture hierarchies.
     */
    private List<DependencyDTO> parents;

    /**
     * Incoming dependencies.
     */
    private List<DependencyDTO> incomingDeps;

    /**
     * Outgoing dependencies.
     */
    private List<DependencyDTO> outgoingDeps;

    /**
     * "Contained Dependencies" are underlying dependencies which are the reason for a dependency in an architecture
     * view.
     */
    private List<DependencyDTO> containedDeps;
}
