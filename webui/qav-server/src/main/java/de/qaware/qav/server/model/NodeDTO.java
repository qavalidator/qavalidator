package de.qaware.qav.server.model;

import com.google.common.base.MoreObjects;

import java.util.List;
import java.util.Map;

/**
 * DTO with all Node details for display at the UI.
 *
 * @author QAware GmbH
 */
public class NodeDTO {

    private String name;

    private Map<String, Object> properties;

    private List<DependencyDTO> parents;
    private List<DependencyDTO> incomingDeps;
    private List<DependencyDTO> outgoingDeps;
    private List<DependencyDTO> containedDeps;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public List<DependencyDTO> getParents() {
        return parents;
    }

    public void setParents(List<DependencyDTO> parents) {
        this.parents = parents;
    }

    public List<DependencyDTO> getIncomingDeps() {
        return incomingDeps;
    }

    public void setIncomingDeps(List<DependencyDTO> incomingDeps) {
        this.incomingDeps = incomingDeps;
    }

    public List<DependencyDTO> getOutgoingDeps() {
        return outgoingDeps;
    }

    public void setOutgoingDeps(List<DependencyDTO> outgoingDeps) {
        this.outgoingDeps = outgoingDeps;
    }

    public List<DependencyDTO> getContainedDeps() {
        return containedDeps;
    }

    public void setContainedDeps(List<DependencyDTO> containedDeps) {
        this.containedDeps = containedDeps;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", name)
                .add("properties", properties)
                .add("parents", parents)
                .add("incomingDeps", incomingDeps)
                .add("outgoingDeps", outgoingDeps)
                .add("containedDeps", containedDeps)
                .toString();
    }
}
