package de.qaware.qav.doc.model;

import lombok.Data;
import lombok.ToString;

/**
 * Base class for the description DTOs.
 *
 * @author QAware GmbH
 */
@Data
@ToString
public abstract class AbstractNameDescriptionDoc {

    private String name;
    private String description;

}
