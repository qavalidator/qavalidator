package de.qaware.qav.doc.processor;

import com.google.common.collect.Lists;
import de.qaware.qav.doc.model.CommandDoc;
import de.qaware.qav.doc.model.ParameterDoc;
import de.qaware.qav.doc.model.PluginDoc;
import de.qaware.qav.util.StringTemplateUtil;
import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Creates the QAvalidator language documentation.
 *
 * Traverses a {@link PluginDocTree} and generates AsciiDoc documentation.
 * Writes the documentation into <tt>.adoc</tt> files using the given {@link DocFileWriter}.
 *
 * @author QAware GmbH
 */
public class DocGenerator {

    private static final String QAVDOC_STG = "/stg/QavDoc.stg";

    private final StringTemplateGroup templates;
    private final DocFileWriter docFileWriter;

    /**
     * Constructor.
     * <p>
     * Tries to load the StringTemplate file.
     *
     * @param docFileWriter utility to write out the documentation
     */
    public DocGenerator(DocFileWriter docFileWriter) {
        this.templates = StringTemplateUtil.loadTemplateGroup(QAVDOC_STG);
        this.docFileWriter = docFileWriter;
    }

    /**
     * Generate the documentation.
     *
     * @param pluginDocTree the {@link PluginDocTree}
     */
    public void generateDoc(PluginDocTree pluginDocTree) {
        for (String pluginName : pluginDocTree.getPluginNames()) {
            StringTemplate pluginDocST = getPluginDoc(pluginName, pluginDocTree.getPlugin(pluginName), pluginDocTree.getCommands(pluginName));
            docFileWriter.writeDocFile(pluginName, pluginDocST.toString());
        }
    }

    private StringTemplate getPluginDoc(String pluginName, PluginDoc qavPluginDoc, List<CommandDoc> qavCommands) {
        StringTemplate pluginST = templates.getInstanceOf("plugin");
        pluginST.setAttribute("name", pluginName);
        pluginST.setAttribute("description", trimDesc(qavPluginDoc.getDescription()));
        pluginST.setAttribute("commands", getCommands(qavCommands));
        return pluginST;
    }

    private List<StringTemplate> getCommands(List<CommandDoc> commandList) {
        // remove duplicated commands.
        List<CommandDoc> commands = commandList.stream().distinct().collect(Collectors.toList());
        commands.sort(Comparator.comparing(CommandDoc::getName));

        List<StringTemplate> result = Lists.newArrayList();
        for (CommandDoc qavCommand: commands) {
            StringTemplate commandST = templates.getInstanceOf("command");
            commandST.setAttribute("name", qavCommand.getName());
            commandST.setAttribute("description", trimDesc(qavCommand.getDescription()));
            commandST.setAttribute("parameters", getParameters(qavCommand));

            String resultText = trimDesc(qavCommand.getResult());
            if (!StringUtils.isBlank(resultText)) {
                commandST.setAttribute("result", resultText);
            }
            result.add(commandST);
        }
        return result;
    }

    private List<StringTemplate> getParameters(CommandDoc qavCommand) {
        List<StringTemplate> params = Lists.newArrayList();
        for (ParameterDoc param : qavCommand.getParameters()) {
            StringTemplate parameterST = templates.getInstanceOf("parameter");
            parameterST.setAttribute("name", param.getName());
            parameterST.setAttribute("description", trimDesc(param.getDescription()));
            params.add(parameterST);
        }

        return params;
    }

    /**
     * Cuts off leading Whitespace, and replaces JavaDoc style "link" references in AsciiDoc syntax (without link)
     *
     * @param s input String
     * @return the trimmed string
     */
    /* package*/
    static String trimDesc(String s) {
        return s.replaceAll("^[ \t]+", "")
                .replaceAll("\\n[ \t]+", "\n")
                .replaceAll("\\{@link[ \t]+(?<name>[\\w.]*)[ \t]*\\}", "`${name}`");
    }
}
