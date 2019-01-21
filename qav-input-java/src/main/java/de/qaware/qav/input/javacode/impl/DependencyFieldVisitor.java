package de.qaware.qav.input.javacode.impl;

import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.Node;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Analyze the field in more detail.
 * <p>
 * Visitor which finds out which other types are referenced, and in which way.
 * So far: analyze the annotations on the field.
 *
 * @author QAware GmbH
 */
public class DependencyFieldVisitor extends FieldVisitor {

    private final DependencyGraph dependencyGraph;
    private final boolean collapseInnerClasses;
    private final Node classNode;
    private final int lineNo;

    /**
     * Constructor.
     *
     * @param dependencyGraph      the {@link DependencyGraph}
     * @param collapseInnerClasses whether to collapse inner classes
     * @param className            the class name
     * @param lineNo               the lin number
     */
    public DependencyFieldVisitor(DependencyGraph dependencyGraph, boolean collapseInnerClasses, String className, int lineNo) {
        super(Opcodes.ASM7);
        this.dependencyGraph = dependencyGraph;
        this.collapseInnerClasses = collapseInnerClasses;
        this.classNode = dependencyGraph.getNode(className);
        this.lineNo = lineNo;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        AnalysisUtil.analyzeAnnotation(dependencyGraph, classNode, desc, visible, collapseInnerClasses, lineNo);
        return null;
    }


}
