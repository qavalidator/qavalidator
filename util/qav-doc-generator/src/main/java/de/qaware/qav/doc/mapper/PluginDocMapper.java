package de.qaware.qav.doc.mapper;

import de.qaware.qav.doc.QavPluginDoc;
import de.qaware.qav.doc.model.PluginDoc;

/**
 * Mapper for {@link PluginDoc} and {@link QavPluginDoc}.
 *
 * @author QAware GmbH
 */
public class PluginDocMapper {

    /**
     * Maps the annotation to the DTO.
     *
     * @param qavPluginDoc the {@link QavPluginDoc} annotation
     * @return the {@link PluginDoc} DTO.
     */
    public PluginDoc toDto(QavPluginDoc qavPluginDoc) {
        PluginDoc result = new PluginDoc();
        result.setName(qavPluginDoc.name());
        result.setDescription(qavPluginDoc.description());
        return result;
    }
}
