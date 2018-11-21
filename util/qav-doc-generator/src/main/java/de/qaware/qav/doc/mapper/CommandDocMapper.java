package de.qaware.qav.doc.mapper;

import de.qaware.qav.doc.QavCommand;
import de.qaware.qav.doc.model.CommandDoc;
import de.qaware.qav.doc.model.ParameterDoc;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapper from the annotations {@link QavCommand} and the nested type <tt>QavCommand.Param</tt> to the DTO {@link
 * CommandDoc}.
 *
 * @author QAware GmbH
 */
public class CommandDocMapper {

    /**
     * Maps the annotation to the DTO.
     *
     * @param qavCommand the {@link QavCommand} annotation
     * @return the {@link CommandDoc} DTO.
     */
    public CommandDoc toDto(QavCommand qavCommand) {
        CommandDoc result = new CommandDoc();
        result.setName(qavCommand.name());
        result.setDescription(qavCommand.description());
        result.setResult(qavCommand.result());

        result.setParameters(getParameterDocs(qavCommand.parameters()));

        return result;
    }

    private List<ParameterDoc> getParameterDocs(QavCommand.Param[] parameters) {
        List<ParameterDoc> result = new ArrayList<>();

        if (parameters != null) {
            for (QavCommand.Param parameter : parameters) {
                result.add(toParameterDoc(parameter));
            }
        }

        return result;
    }

    private ParameterDoc toParameterDoc(QavCommand.Param qavParam) {
        ParameterDoc result = new ParameterDoc();
        result.setName(qavParam.name());
        result.setDescription(qavParam.description());
        return result;
    }
}
