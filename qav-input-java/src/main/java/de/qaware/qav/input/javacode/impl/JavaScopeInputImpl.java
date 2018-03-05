package de.qaware.qav.input.javacode.impl;

import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.input.javacode.api.JavaScopeInput;

import java.util.Map;

/**
 * Uses {@link JavaScopeReader} to implement the {@link JavaScopeInput} interface.
 *
 * @author QAware GmbH
 */
public class JavaScopeInputImpl implements JavaScopeInput {

    @Override
    public void read(DependencyGraph dependencyGraph, boolean collapseInnerClasses, Map parameters) {
        new JavaScopeReader(dependencyGraph, collapseInnerClasses).read(parameters);
    }
}
