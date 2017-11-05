package de.qaware.qav.server.model;

import java.util.List;
import java.util.Map;

/**
 * DTO with all Dependency details for display at the UI.
 *
 * @author QAware GmbH
 */
public class DependencyDTO {

    private String sourceName;
    private String targetName;
    private String typeName;

    private List<DependencyDTO> baseDependencies;
    private Map<String, Object> properties;

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public List<DependencyDTO> getBaseDependencies() {
        return baseDependencies;
    }

    public void setBaseDependencies(List<DependencyDTO> baseDependencies) {
        this.baseDependencies = baseDependencies;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
}
