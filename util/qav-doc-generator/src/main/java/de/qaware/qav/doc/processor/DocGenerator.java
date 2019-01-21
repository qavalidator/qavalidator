package de.qaware.qav.doc.processor;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import de.qaware.qav.doc.model.CommandDoc;
import de.qaware.qav.doc.model.ParameterDoc;
import de.qaware.qav.doc.model.PluginDoc;
import de.qaware.qav.util.StringTemplateUtil;
import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Creates the QAvalidator language documentation.
 * <p>
 * Traverses a {@link PluginDocTree} and generates AsciiDoc documentation. Writes the documentation into <tt>.adoc</tt>
 * files using the given {@link DocFileWriter}.
 *
 * @author QAware GmbH
 */
public class DocGenerator {

    private static final String QAVDOC_STG = "/stg/QavDoc.stg";

    // StringTemplate attribute names:
    private static final String DESCRIPTION_ATT = "description";
    private static final String NAME_ATT = "name";
    private static final String COMMANDS_ATT = "commands";
    private static final String PARAMETERS_ATT = "parameters";
    private static final String RESULT_ATT = "result";

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
        this.templates = StringTemplateUtil.loadTemplateGroupAngleBracket(QAVDOC_STG);
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
        pluginST.setAttribute(NAME_ATT, pluginName);
        pluginST.setAttribute(DESCRIPTION_ATT, trimDesc(qavPluginDoc.getDescription()));
        pluginST.setAttribute(COMMANDS_ATT, getCommands(qavCommands));
        return pluginST;
    }

    private List<StringTemplate> getCommands(List<CommandDoc> commandList) {
        // remove duplicated commands.
        List<CommandDoc> commands = commandList.stream()
                .distinct()
                .sorted(Comparator.comparing(CommandDoc::getName))
                .collect(Collectors.toList());

        List<StringTemplate> result = Lists.newArrayList();
        for (CommandDoc qavCommand : commands) {
            StringTemplate commandST = templates.getInstanceOf("command");
            commandST.setAttribute(NAME_ATT, qavCommand.getName());
            commandST.setAttribute(DESCRIPTION_ATT, trimDesc(qavCommand.getDescription()));
            commandST.setAttribute(PARAMETERS_ATT, getParameters(qavCommand));

            String resultText = trimDesc(qavCommand.getResult());
            if (!Strings.isNullOrEmpty(resultText)) {
                commandST.setAttribute(RESULT_ATT, resultText);
            }
            result.add(commandST);
        }
        return result;
    }

    private List<StringTemplate> getParameters(CommandDoc qavCommand) {
        List<StringTemplate> params = Lists.newArrayList();
        for (ParameterDoc param : qavCommand.getParameters()) {
            StringTemplate parameterST = templates.getInstanceOf("parameter");
            parameterST.setAttribute(NAME_ATT, param.getName());
            parameterST.setAttribute(DESCRIPTION_ATT, trimDesc(param.getDescription()));
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
    @SuppressWarnings("squid:S4248") // wants to move the RegExes into constants; would not help readability here.
    static String trimDesc(String s) {
        return s.replaceAll("^[ \t]+", "")
                .replaceAll("\\n[ \t]+", "\n")
                .replaceAll("\\{@link[ \t]+(?<name>[\\w.]*)[ \t]*}", "`${name}`");
    }
}
