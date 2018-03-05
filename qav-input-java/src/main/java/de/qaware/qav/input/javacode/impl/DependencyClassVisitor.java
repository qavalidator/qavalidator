package de.qaware.qav.input.javacode.impl;

import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.Node;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static de.qaware.qav.graph.api.Constants.TYPE;
import static de.qaware.qav.graph.api.Constants.TYPE_CLASS;
import static de.qaware.qav.input.javacode.impl.DependencyUtil.isIgnorable;

/**
 * This visitor collects all dependencies on the class level and sends off a method visitor for each method.
 *
 * @author QAware GmbH
 */
public class DependencyClassVisitor extends ClassVisitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DependencyClassVisitor.class);

    private final DependencyGraph dependencyGraph;
    private final boolean collapseInnerClasses;
    private String className;
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

        addInheritanceDependency(superName);
        Arrays.stream(interfaces).forEach(this::addInheritanceDependency);
    }

    public String getClassName() {
        return className;
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

        String typeName = AsmUtil.toClassName(Type.getType(desc).getClassName(), collapseInnerClasses);
        addParameterTypeDependency(typeName);

        analyzeSignature(name, signature);

        return new DependencyFieldVisitor(dependencyGraph, collapseInnerClasses, className, 0);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        LOGGER.debug("Original method info: Access: {}, Name: {}, Desc: {}, Signature: {}, Exceptions: {}",
                access, name, desc, signature, exceptions == null ? "null" : Arrays.asList(exceptions));

        String returnTypeName = AsmUtil.toClassName(Type.getReturnType(desc).getClassName(), collapseInnerClasses);
        List<String> paramTypes = AsmUtil.getParameterTypeNames(desc, collapseInnerClasses);
        LOGGER.debug("Method (plain): {} {}#{}({})", returnTypeName, className, name, paramTypes);
        addParameterTypeDependency(returnTypeName);
        paramTypes.forEach(this::addParameterTypeDependency);

        analyzeSignature(name, signature);

        if (exceptions != null) {
            Arrays.stream(exceptions).forEach(it -> addParameterTypeDependency(AsmUtil.toClassName(it, collapseInnerClasses)));
        }

        return new DependencyMethodVisitor(dependencyGraph, className, name, collapseInnerClasses);
    }

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


    private void addInheritanceDependency(String parentName) {
        String parentClassName = AsmUtil.toClassName(parentName, collapseInnerClasses);
        if (!isIgnorable(parentClassName)) {
            Node superNode = dependencyGraph.getOrCreateNodeByName(parentClassName);
            superNode.setProperty(TYPE, TYPE_CLASS);
            dependencyGraph.addDependency(classNode, superNode, DependencyType.INHERIT);
        }
    }

    private void addParameterTypeDependency(String targetClassName) {
        if (!className.equals(targetClassName) && !isIgnorable(targetClassName)) {
            LOGGER.debug("Add dependency: {} --[{}]--> {}", className, DependencyType.REFERENCE, targetClassName);
            Node targetNode = dependencyGraph.getOrCreateNodeByName(targetClassName);
            dependencyGraph.addDependency(classNode, targetNode, DependencyType.REFERENCE);
        }
    }
}
