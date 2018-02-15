package de.qaware.qav.doc.model;

import lombok.Data;
import lombok.ToString;

/**
 * DTO for Parameter documentation.
 *
 * @author QAware GmbH
 */
@Data
@ToString
public class ParameterDoc {

    private String name;
    private String description;
}
