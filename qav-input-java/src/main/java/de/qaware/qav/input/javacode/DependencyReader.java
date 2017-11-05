package de.qaware.qav.input.javacode;

import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.Node;
import org.objectweb.asm.ClassReader;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Read dependencies from .class files.
 *
 * @author QAware GmbH
 */
public class DependencyReader {

    private final DependencyGraph dependencyGraph;
    private final boolean collapseInnerClasses;

    /**
     * Constructor.
     *
     * @param dependencyGraph      the graph to fill
     * @param collapseInnerClasses whether or not collapse inner classes.
     *                             True: Use the outer class; false means deal with the full name
     */
    public DependencyReader(DependencyGraph dependencyGraph, boolean collapseInnerClasses) {
        this.dependencyGraph = dependencyGraph;
        this.collapseInnerClasses = collapseInnerClasses;
    }

    /**
     * Reads the dependencies from the given file.
     *
     * @param classAsBytes the class content, as byte array
     * @return the node representing the given class file.
     * @throws IllegalStateException if the given file can not be read.
     */
    public Node readDependencies(byte[] classAsBytes) {
        checkNotNull(classAsBytes, "Class could not be read");

        DependencyClassVisitor visitor = new DependencyClassVisitor(dependencyGraph, collapseInnerClasses);
        ClassReader reader = new ClassReader(classAsBytes);
        reader.accept(visitor, 0);

        return dependencyGraph.getNode(visitor.getClassName());
    }
}
