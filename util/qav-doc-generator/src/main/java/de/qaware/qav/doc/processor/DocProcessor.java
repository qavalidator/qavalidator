package de.qaware.qav.doc.processor;

import de.qaware.qav.doc.QavCommand;
import de.qaware.qav.doc.QavPluginDoc;
import de.qaware.qav.doc.mapper.CommandDocMapper;
import de.qaware.qav.doc.mapper.PluginDocMapper;
import de.qaware.qav.util.FileNameUtil;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

/**
 * Use annotation processing to create documentation.
 * <p>
 * So far: This works. However, Groovy does not offer the JavaDocs; that only works on Java code.
 * It would be a line like this:
 * <p>
 * <code>
 * String docComment = processingEnv.getElementUtils().getDocComment(e);
 * </code>
 * <p>
 * Therefore we use a workaround and add "description" fields etc to the annotation.
 *
 * @author QAware GmbH
 */
@SupportedAnnotationTypes(
        {
                "de.qaware.qav.doc.QavCommand",
                "de.qaware.qav.doc.QavPluginDoc"
        }
)
@SupportedOptions(DocProcessor.GEN_DOC_DIR_OPTION)
public class DocProcessor extends AbstractProcessor {

    /**
     * option name to set the "generated-docs" directory.
     */
    public static final String GEN_DOC_DIR_OPTION = "genDocDir";

    private static final String DEFAULT_OUTPUT_DIR = "../qav-doc/src-gen/generated-docs";

    private final CommandDocMapper commandDocMapper = new CommandDocMapper();
    private final PluginDocMapper pluginDocMapper = new PluginDocMapper();

    private String outputDir = DEFAULT_OUTPUT_DIR;

    private Messager messager;
    private DocGenerator docGenerator;

    /**
     * setter for the output directory.
     * Only used for testing.
     *
     * @param outputDir the output directory
     */
    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();

        this.outputDir = processingEnv.getOptions().get(GEN_DOC_DIR_OPTION);
        if (this.outputDir ==  null) {
            messager.printMessage(Diagnostic.Kind.WARNING, "No genDocDir set. Using default: " + DEFAULT_OUTPUT_DIR);
            this.outputDir = DEFAULT_OUTPUT_DIR;
        }
        this.outputDir = FileNameUtil.getCanonicalPath(this.outputDir);
        docGenerator = new DocGenerator(new DocFileWriter(this::logError, this.outputDir));
    }

    /**
     * Iterates over the annotations, processes all elements and puts them into a {@link PluginDocTree}.
     * Generates the output from that {@link PluginDocTree}.
     *
     * @param annotations the annotation types requested to be processed
     * @param roundEnv    environment for information about the current and prior round
     * @return always true since this method uses the given annotations
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        PluginDocTree pluginDocTree = new PluginDocTree();

        for (TypeElement te : annotations) {
            for (Element e : roundEnv.getElementsAnnotatedWith(te)) {
                processElement(pluginDocTree, e);
            }
        }

        docGenerator.generateDoc(pluginDocTree);

        return true;
    }

    /**
     * Analyze the element and put the information into the {@link PluginDocTree}.
     * <p>
     * So in the end, the {@link PluginDocTree} will contain all the documentation that the {@link DocGenerator} needs
     * to generate the documentation.
     *
     * @param pluginDocTree the target {@link PluginDocTree} which stores all documentation information
     * @param element       the {@link Element} to analyze
     */
    private void processElement(PluginDocTree pluginDocTree, Element element) {
        Element parentElement = element.getEnclosingElement();

        if (parentElement.getKind() == ElementKind.CLASS) {
            QavCommand qavCommand = element.getAnnotation(QavCommand.class);
            pluginDocTree.addCommand(parentElement.getSimpleName().toString(), commandDocMapper.toDto(qavCommand));
        } else if (parentElement.getKind() == ElementKind.PACKAGE) {
            QavPluginDoc plugin = element.getAnnotation(QavPluginDoc.class);
            pluginDocTree.addPlugin(element.getSimpleName().toString(), pluginDocMapper.toDto(plugin));
        }
    }

    /**
     * Use the compiler's messager to write error messages.
     *
     * @param message the message to write
     */
    private void logError(String message) {
        messager.printMessage(Diagnostic.Kind.ERROR, message);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
