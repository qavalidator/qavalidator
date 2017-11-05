package de.qaware.qav.input.javacode;

import com.google.common.collect.Lists;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureVisitor;

import java.util.List;

/**
 * Visitor for method signatures or field signatures.
 *
 * @author QAware GmbH
 */
public class MethodFieldSignatureVisitor extends SignatureVisitor {

    private final List<String> paramTypes = Lists.newArrayList();
    private final List<String> returnTypes = Lists.newArrayList();
    private final boolean collapseInnerClasses;

    private List<String> next = returnTypes;

    /**
     * Constructor.
     *
     * @param collapseInnerClasses whether or not collapse inner classes.
     *                             True: Use the outer class; false means deal with the full name
     */
    public MethodFieldSignatureVisitor(boolean collapseInnerClasses) {
        super(Opcodes.ASM5);
        this.collapseInnerClasses = collapseInnerClasses;
    }

    @Override
    public SignatureVisitor visitParameterType() {
        next = paramTypes;
        return this;
    }

    @Override
    public SignatureVisitor visitReturnType() {
        next = returnTypes;
        return this;
    }

    @Override
    public void visitClassType(String name) {
        next.add(AsmUtil.toClassName(name, collapseInnerClasses));
    }

    @Override
    public void visitBaseType(char descriptor) {
        next.add(AsmUtil.toClassName(Type.getType(String.valueOf(descriptor)).getClassName(), collapseInnerClasses));
    }

    public List<String> getParamTypes() {
        return paramTypes;
    }

    /**
     * Returns the names of the return type of the method.
     * In case of generic types this list may have more than one entry
     * (e.g. a Set of String will be a list with "Set" and "String").
     *
     * @return the name of the return type
     */
    public List<String> getReturnTypes() {
        if (returnTypes.isEmpty()) {
            return Lists.newArrayList("void");
        } else {
            return returnTypes;
        }
    }
}
