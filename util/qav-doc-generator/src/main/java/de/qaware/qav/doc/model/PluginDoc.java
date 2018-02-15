package de.qaware.qav.doc.model;

import lombok.Data;
import lombok.ToString;

/**
 * DTO for Plugin documentation.
 *
 * @author QAware GmbH
 */
@Data
@ToString
public class PluginDoc {

    private String name;
    private String description;
}
