package de.qaware.qav.input.javacode.impl;

import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.Node;
import lombok.extern.slf4j.Slf4j;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.List;

import static de.qaware.qav.graph.api.Constants.LINE_NO;
import static de.qaware.qav.graph.api.Constants.TYPE;
import static de.qaware.qav.graph.api.Constants.TYPE_CLASS;
import static de.qaware.qav.graph.api.Constants.TYPE_METHOD;
import static de.qaware.qav.input.javacode.impl.DependencyUtil.isIgnorable;

/**
 * Method Visitor which is called for each method and finds out which other types are referenced, and in which way.
 * <p>
 * References can be:
 * <p>
 * Annotations on the method, Annotations on method parameters, Method parameter types, The return type, Instructions
 * which call methods on other types, Exception types in a try/catch block.
 *
 * @author QAware GmbH
 */
@Slf4j
public class DependencyMethodVisitor extends MethodVisitor {

    private static final String METHOD_SEPARATOR = "::";

    private final DependencyGraph dependencyGraph;
    private final boolean collapseInnerClasses;
    private final String className;
    private final Node classNode;
    private final String methodName;
    private final Node methodNode;

    /**
     * The currently analyzed line number.
     * <p>
     * It is set in {@link #visitLineNumber(int, Label)}, and is added to each dependency with the property name {@link
     * de.qaware.qav.graph.api.Constants#LINE_NO}.
     */
    private int lineNo = 0;

    /**
     * Constructor.
     *
     * @param dependencyGraph      the {@link DependencyGraph} to write the dependencies to
     * @param fullClassName        full name of the class to visit (i.e., not collapsed!)
     * @param methodName           name of the method to visit
     * @param collapseInnerClasses <tt>true</tt> to collapse inner classes onto the outer class
     */
    public DependencyMethodVisitor(DependencyGraph dependencyGraph, String fullClassName, String methodName, boolean collapseInnerClasses) {
        super(Opcodes.ASM7);
        this.dependencyGraph = dependencyGraph;
        this.className = AsmUtil.toClassName(fullClassName, collapseInnerClasses);
        this.methodName = methodName;
        this.collapseInnerClasses = collapseInnerClasses;

        this.classNode = dependencyGraph.getOrCreateNodeByName(className);
        this.classNode.setProperty(TYPE, TYPE_CLASS);

        // create a node for the method, and add a CONTAINS relation from the owning class
        this.methodNode = getMethodNode(fullClassName, methodName);
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

        // add a dependency to the class of the called method
        DependencyType dependencyType = getDependencyType(name);
        addTypeDependency(owner, dependencyType);

        // for each parameter of the called method: add a dependency to the type of that parameter
        List<String> paramTypes = AsmUtil.getParameterTypeNames(desc, collapseInnerClasses);
        paramTypes.forEach(it -> addTypeDependency(it, DependencyType.REFERENCE));

        // add a dependency on method level
        addMethodDependency(owner, name, dependencyType);
    }

    /**
     * Apply heuristics to define the {@link DependencyType}.
     * <p>
     * It's only based on the name, not on what the method actually does. I.e. {@link DependencyType#READ_ONLY} access
     * is defined solely on the name; it's not checked whether the method has side effects.
     *
     * @param methodName the method name
     * @return the {@link DependencyType}
     */
    private DependencyType getDependencyType(String methodName) {
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
            addTypeDependency(targetName, DependencyType.REFERENCE);
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
                addTypeDependency(ownerClassName, DependencyType.READ_WRITE);
            } else if (opcode == Opcodes.GETFIELD || opcode == Opcodes.GETSTATIC) {
                addTypeDependency(ownerClassName, DependencyType.READ_ONLY);
            } else {
                addTypeDependency(ownerClassName, DependencyType.REFERENCE);
            }
        }
    }

    @Override
    public void visitLdcInsn(Object value) {
        if (value instanceof Type) {
            Type type = (Type) value;
            addTypeDependency(type.getInternalName(), DependencyType.REFERENCE);
        }
    }

    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        LOGGER.debug("Line: {}, start: {}, end: {}, handler: {}, type: {}", lineNo, start, end, handler, type);

        // type is null for finally blocks
        if (type != null) {
            LOGGER.debug("TryCatchBlock: type: {}", type);
            addTypeDependency(type, DependencyType.READ_ONLY);
        }
    }

    @Override
    public void visitLineNumber(int line, Label start) {
        this.lineNo = line;
    }


    /**
     * Add the dependency from {@link #classNode} to the target node with the given name.
     * <p>
     * If the target name is equal to the {@link #className}, or if the target name is ignorable, then the method does
     * nothing.
     *
     * @param targetName     the name of the target node
     * @param dependencyType the {@link DependencyType}
     */
    private void addTypeDependency(String targetName, DependencyType dependencyType) {
        String targetClassName = AsmUtil.toClassName(targetName, collapseInnerClasses);
        if (!className.equals(targetClassName) && !isIgnorable(targetClassName)) {
            LOGGER.debug("Add dependency: {}#{} --[{}]--> {}", className, methodName, dependencyType, targetClassName);
            Node targetNode = dependencyGraph.getOrCreateNodeByName(targetClassName);
            targetNode.setProperty(TYPE, TYPE_CLASS);
            dependencyGraph.addDependency(classNode, targetNode, dependencyType)
                    .addListProperty(LINE_NO, lineNo);
        }
    }

    /**
     * Add the dependency from the {@link #methodNode} to the target node for the given called method.
     * <p>
     * Do so only if the target class is not ignorable. This way, we don't collect calls to methods in classes which
     * we're not interested in. The method name is always constructed with the full class name, i.e. inner class names
     * are never folded (regardless of {@link #collapseInnerClasses}).
     *
     * @param targetClassName  class name which owns the called method
     * @param targetMethodName the name of the called method
     * @param dependencyType   the {@link DependencyType}
     */
    private void addMethodDependency(String targetClassName, String targetMethodName, DependencyType dependencyType) {
        if (!isIgnorable(targetClassName)) {
            Node targetMethodNode = getMethodNode(targetClassName, targetMethodName);
            LOGGER.debug("Add dependency: {} --[{}]--> {}", methodNode.getName(), dependencyType, targetMethodNode.getName());
            dependencyGraph.addDependency(methodNode, targetMethodNode, dependencyType)
                    .addListProperty(LINE_NO, lineNo);
        }
    }

    /**
     * Get the node for the method, and add the CONTAINS relation from the owning class (i.e., where the method is
     * defined). Creates the method node, if necessary, and the class node, if necessary.
     * <p>
     * Set "type" properties on these nodes, for "class" and "method", respectively.
     *
     * @param className  the class which defines the method
     * @param methodName the method name
     * @return the {@link Node}
     */
    private Node getMethodNode(String className, String methodName) {
        String fqMethodName = AsmUtil.toClassName(className, false) + METHOD_SEPARATOR + methodName;
        Node result = dependencyGraph.getOrCreateNodeByName(fqMethodName);
        result.setProperty(TYPE, TYPE_METHOD);

        Node owningClassNode = dependencyGraph.getOrCreateNodeByName(AsmUtil.toClassName(className, collapseInnerClasses));
        owningClassNode.setProperty(TYPE, TYPE_CLASS);
        dependencyGraph.addDependency(owningClassNode, result, DependencyType.CONTAINS);

        return result;
    }
}
