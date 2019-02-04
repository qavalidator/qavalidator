package de.qaware.qav.app.server.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * DTO with all Dependency details for display at the UI.
 *
 * @author QAware GmbH
 */
@Data
public class DependencyDTO {

    private String sourceName;
    private String targetName;
    private String typeName;

    private List<DependencyDTO> baseDependencies;
    private Map<String, Object> properties;

}
