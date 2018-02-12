package de.qaware.qav.doc.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO for Command documentation.
 *
 * @author QAware GmbH
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString
public class CommandDoc extends AbstractNameDescriptionDoc {

    private List<ParameterDoc> parameters = new ArrayList<>();
    private String result;

    /**
     * Add parameters.
     *
     * @param parameters parameters to add
     */
    public void setParameters(List<ParameterDoc> parameters) {
        this.parameters.addAll(parameters);
    }

}
