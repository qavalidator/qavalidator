package de.qaware.qav.input.javacode.impl;

import de.qaware.qav.graph.api.Dependency;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.Node;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;

import java.util.Arrays;
import java.util.List;

import static de.qaware.qav.graph.api.Constants.TYPE;
import static de.qaware.qav.graph.api.Constants.TYPE_CLASS;
import static de.qaware.qav.input.javacode.impl.DependencyUtil.isIgnorable;

/**
 * This visitor collects all dependencies on the class level:
 * <p>
 * 1. analyze all fields
 * <p>
 * 2. analyze all methods and their signatures, i.e. return type and parameter types
 * <p>
 * 3. send off a method visitor for each method
 *
 * @author QAware GmbH
 */
@Slf4j
public class DependencyClassVisitor extends ClassVisitor {

    private final DependencyGraph dependencyGraph;
    private final boolean collapseInnerClasses;

    /**
     * The name of the currently analyzed class. Will be set in the call to the {@link #visit(int, int, String, String,
     * String, String[])} method.
     */
    @Getter
    private String className;

    /**
     * The {@link Node} representing the currently analyzed class.
     */
    private Node classNode;

    /**
     * Constructor.
     *
     * @param dependencyGraph      the graph to fill
     * @param collapseInnerClasses whether or not collapse inner classes. True: Use the outer class; false means deal
     *                             with the full name
     */
    public DependencyClassVisitor(DependencyGraph dependencyGraph, boolean collapseInnerClasses) {
        super(Opcodes.ASM5);
        this.dependencyGraph = dependencyGraph;
        this.collapseInnerClasses = collapseInnerClasses;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        LOGGER.debug("Class:  Version: {}, Access: {}, Name: {}, Signature: {}, superName: {}, Interfaces: {}",
                version, access, name, signature, superName, Arrays.asList(interfaces));

        this.className = AsmUtil.toClassName(name, collapseInnerClasses);
        this.classNode = dependencyGraph.getOrCreateNodeByName(className);
        this.classNode.setProperty(TYPE, TYPE_CLASS);

        analyzeSignature(name, signature);

        // Inheritance: may be one class an many interfaces:
        addInheritanceDependency(superName);
        Arrays.stream(interfaces).forEach(this::addInheritanceDependency);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        AnalysisUtil.analyzeAnnotation(dependencyGraph, classNode, desc, visible, collapseInnerClasses, 0);
        return null;
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        LOGGER.debug("Original field info: Access: {}, Name: {}, Desc: {}, Signature: {}, Value: {}",
                access, name, desc, signature, value == null ? "null" : value);

        addParameterTypeDependency(Type.getType(desc).getClassName());

        analyzeSignature(name, signature);

        return new DependencyFieldVisitor(dependencyGraph, collapseInnerClasses, className, 0);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        LOGGER.debug("Original method info: Access: {}, Name: {}, Desc: {}, Signature: {}, Exceptions: {}",
                access, name, desc, signature, exceptions == null ? "null" : Arrays.asList(exceptions));

        String returnTypeName = Type.getReturnType(desc).getClassName();
        List<String> paramTypes = AsmUtil.getParameterTypeNames(desc, collapseInnerClasses);
        LOGGER.debug("Method (plain): {} {}#{}({})", returnTypeName, className, name, paramTypes);
        addParameterTypeDependency(returnTypeName);
        paramTypes.forEach(this::addParameterTypeDependency);

        analyzeSignature(name, signature);

        if (exceptions != null) {
            Arrays.stream(exceptions).forEach(this::addParameterTypeDependency);
        }

        return new DependencyMethodVisitor(dependencyGraph, className, name, collapseInnerClasses);
    }

    /**
     * Analyze the given signature and add the dependencies.
     *
     * @param name      class, method, or field name
     * @param signature signature String
     */
    private void analyzeSignature(String name, String signature) {
        if (signature != null) {
            SignatureReader sr = new SignatureReader(signature);
            MethodFieldSignatureVisitor v = new MethodFieldSignatureVisitor(collapseInnerClasses);
            sr.accept(v);

            List<String> sigReturnTypeNames = v.getReturnTypes();
            List<String> signatureParams = v.getParamTypes();
            LOGGER.debug("Signature: {} {}#{}({})", sigReturnTypeNames, className, name, signatureParams);

            signatureParams.forEach(this::addParameterTypeDependency);
            sigReturnTypeNames.forEach(this::addParameterTypeDependency);
        }
    }

    /**
     * Add inheritance dependency.
     * <p>
     * Normalizes the target node name to use.
     * <p>
     * Set the property <tt>type</tt> to <tt>class</tt> on the parent node.
     *
     * @param parentName the name of the parent class
     */
    private void addInheritanceDependency(String parentName) {
        String parentClassName = AsmUtil.toClassName(parentName, collapseInnerClasses);
        if (!isIgnorable(parentClassName)) {
            Node superNode = dependencyGraph.getOrCreateNodeByName(parentClassName);
            superNode.setProperty(TYPE, TYPE_CLASS);
            Dependency dependency = dependencyGraph.addDependency(classNode, superNode, DependencyType.INHERIT);
            LOGGER.debug("Add dependency: {}", dependency);
        }
    }

    /**
     * Add a type dependency, i.e. using {@link DependencyType#REFERENCE}.
     * <p>
     * Normalizes the target node name to use.
     *
     * @param targetName the name of the target class
     */
    private void addParameterTypeDependency(String targetName) {
        String targetClassName = AsmUtil.toClassName(targetName, collapseInnerClasses);
        if (!className.equals(targetClassName) && !isIgnorable(targetClassName)) {
            Node targetNode = dependencyGraph.getOrCreateNodeByName(targetClassName);
            Dependency dependency = dependencyGraph.addDependency(classNode, targetNode, DependencyType.REFERENCE);
            LOGGER.debug("Add dependency: {}", dependency);
        }
    }
}
