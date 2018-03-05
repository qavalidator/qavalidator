package de.qaware.qav.input.javacode.impl;

import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.Node;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static de.qaware.qav.graph.api.Constants.LINE_NO;
import static de.qaware.qav.graph.api.Constants.TYPE;
import static de.qaware.qav.graph.api.Constants.TYPE_CLASS;
import static de.qaware.qav.input.javacode.impl.DependencyUtil.isIgnorable;

/**
 * Method Visitor which is called for each method and finds out which other types are referenced, and in which way.
 *
 * @author QAware GmbH
 */
public class DependencyMethodVisitor extends MethodVisitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DependencyMethodVisitor.class);

    private final DependencyGraph dependencyGraph;
    private final boolean collapseInnerClasses;
    private final String className;
    private final Node classNode;
    private final String methodName;

    private int lineNo = 0;

    /**
     * Constructor.
     *
     * @param dependencyGraph      the {@link DependencyGraph} to write the dependencies to
     * @param className            name of the class to visit
     * @param methodName           name of the method to visit
     * @param collapseInnerClasses <tt>true</tt> to collapse inner classes onto the outer class
     */
    public DependencyMethodVisitor(DependencyGraph dependencyGraph, String className, String methodName, boolean collapseInnerClasses) {
        super(Opcodes.ASM5);
        this.className = className;
        this.methodName = methodName;
        this.dependencyGraph = dependencyGraph;
        this.classNode = dependencyGraph.getOrCreateNodeByName(className);
        this.classNode.setProperty(TYPE, TYPE_CLASS);
        this.collapseInnerClasses = collapseInnerClasses;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        LOGGER.debug("visitAnnotation: desc: {}, visible: {}", desc, visible);
        AnalysisUtil.analyzeAnnotation(dependencyGraph, classNode, desc, visible, collapseInnerClasses, lineNo);
        return null;
    }

    @Override
    public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
        LOGGER.debug("visitParameterAnnotation: parameter: {}, desc: {}, visible: {}", parameter, desc, visible);
        AnalysisUtil.analyzeAnnotation(dependencyGraph, classNode, desc, visible, collapseInnerClasses, lineNo);
        return null;
    }


    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        LOGGER.debug("Line: {}, Opcode: {}, owner: {}, name: {}, desc: {}, itf: {}", lineNo, opcode, owner, name, desc, itf);

        List<String> paramTypes = AsmUtil.getParameterTypeNames(desc, collapseInnerClasses);

        DependencyType dependencyType = getDepType(name);
        String targetClassName = AsmUtil.toClassName(owner, collapseInnerClasses);

        if (isIgnorable(targetClassName)) {
            LOGGER.debug("Skipping reference from {} to {}", className, targetClassName);
        } else if (targetClassName.equals(className)) {
            LOGGER.debug("Skipping self-reference: {} [{}]", className, dependencyType);
        } else {
            addDependency(targetClassName, dependencyType);
            paramTypes.forEach(it -> addDependency(it, DependencyType.REFERENCE));
        }
    }

    private DependencyType getDepType(String methodName) {
        if ("<init>".equals(methodName) || "<clinit>".equals(methodName)) {
            return DependencyType.CREATE;
        }
        if ("get".equals(methodName) || "is".equals(methodName) ||
                isGetterName(methodName, "get") || isGetterName(methodName, "is")) {
            return DependencyType.READ_ONLY;
        }

        return DependencyType.READ_WRITE;
    }

    private boolean isGetterName(String methodName, String prefix) {
        return methodName.startsWith(prefix) && Character.isUpperCase(methodName.charAt(prefix.length()));
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        String targetName = AsmUtil.toClassName(type, collapseInnerClasses);
        if (opcode == Opcodes.INSTANCEOF && !isIgnorable(targetName)) {
            addDependency(targetName, DependencyType.REFERENCE);
        }
    }

    /**
     * Create a dependency for direct field assignments.
     * <p>
     * {@inheritDoc}
     */
    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        LOGGER.debug("Line: {}, Opcode: {}, owner: {}, name: {}, desc: {}", lineNo, opcode, owner, name, desc);

        String ownerClassName = AsmUtil.toClassName(owner, collapseInnerClasses);
        if (!className.equals(ownerClassName) && !isIgnorable(ownerClassName)) {
            if (opcode == Opcodes.PUTFIELD || opcode == Opcodes.PUTSTATIC) {
                addDependency(ownerClassName, DependencyType.READ_WRITE);
            } else if (opcode == Opcodes.GETFIELD || opcode == Opcodes.GETSTATIC) {
                addDependency(ownerClassName, DependencyType.READ_ONLY);
            } else {
                addDependency(ownerClassName, DependencyType.REFERENCE);
            }
        }
    }

    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        LOGGER.debug("Line: {}, start: {}, end: {}, handler: {}, type: {}", lineNo, start, end, handler, type);

        // type is null for finally blocks
        if (type != null) {
            LOGGER.debug("TryCatchBlock: type: {}", type);
            addDependency(AsmUtil.toClassName(type, collapseInnerClasses), DependencyType.READ_ONLY);
        }
    }

    @Override
    public void visitLineNumber(int line, Label start) {
        this.lineNo = line;
    }

    private void addDependency(String targetClassName, DependencyType dependencyType) {
        if (!className.equals(targetClassName) && !isIgnorable(targetClassName)) {
            LOGGER.debug("Add dependency: {}#{} --[{}]--> {}", className, methodName, dependencyType, targetClassName);
            Node targetNode = dependencyGraph.getOrCreateNodeByName(targetClassName);
            targetNode.setProperty(TYPE, TYPE_CLASS);
            dependencyGraph.addDependency(classNode, targetNode, dependencyType).addListProperty(LINE_NO, lineNo);
        }
    }
}
