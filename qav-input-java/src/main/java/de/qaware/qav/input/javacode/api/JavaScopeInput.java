package de.qaware.qav.input.javacode.api;

import de.qaware.qav.graph.api.DependencyGraph;

import java.util.Map;

/**
 * Read the given <tt>.class</tt> files.
 *
 * @author QAware GmbH
 */
public interface JavaScopeInput {

    /**
     * Read the files as defined by the parameters into the given graph.
     *
     * @param dependencyGraph      the graph where to write the classes and their dependencies
     * @param collapseInnerClasses if true, collapses the dependencies of an inner class onto the outer class
     * @param parameters           the parameters, Ant-style with baseDir (mandatory), and includes (optional) and
     *                             excludes (optional)
     */
    void read(DependencyGraph dependencyGraph, boolean collapseInnerClasses, Map parameters);
}
