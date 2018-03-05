package de.qaware.qav.input.javacode.impl;

import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.Node;
import org.slf4j.Logger;

import static de.qaware.qav.graph.api.Constants.ANNOTATION;
import static de.qaware.qav.graph.api.Constants.LINE_NO;
import static de.qaware.qav.graph.api.Constants.TYPE;
import static de.qaware.qav.graph.api.Constants.TYPE_CLASS;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Do analysis of annotations, and put the dependencies into the {@link DependencyGraph}.
 *
 * @author QAware GmbH
 */
public final class AnalysisUtil {

    private static final Logger LOGGER = getLogger(AnalysisUtil.class);

    /**
     * util class, no instances.
     */
    private AnalysisUtil() {
    }

    /**
     * Analyze the annotation, and put the dependencies into the {@link DependencyGraph}.
     *
     * @param dependencyGraph      the {@link DependencyGraph}
     * @param classNode            the node representing the source class
     * @param desc                 the ASM description
     * @param visible              true if the annotation is visible at runtime; only used for logging
     * @param collapseInnerClasses whether to collapse inner classes
     * @param lineNo               the line number
     */
    public static void analyzeAnnotation(DependencyGraph dependencyGraph, Node classNode, String desc, boolean visible, boolean collapseInnerClasses, int lineNo) {
        String annotationTypeName = AsmUtil.toClassName(desc, collapseInnerClasses);
        LOGGER.debug("Original method annotation info: Desc: {}, Visible: {} => {}", desc, visible, annotationTypeName);

        Node annotationNode = dependencyGraph.getOrCreateNodeByName(annotationTypeName);
        annotationNode.setProperty(TYPE, TYPE_CLASS);
        annotationNode.setProperty(ANNOTATION, true);
        dependencyGraph.addDependency(classNode, annotationNode, DependencyType.ANNOTATED_BY).addListProperty(LINE_NO, lineNo);
    }
}
