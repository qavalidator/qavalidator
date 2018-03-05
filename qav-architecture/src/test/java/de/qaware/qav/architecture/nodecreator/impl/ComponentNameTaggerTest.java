package de.qaware.qav.architecture.nodecreator.impl;

import de.qaware.qav.architecture.dsl.model.Architecture;
import de.qaware.qav.architecture.factory.DefaultPackageArchitectureFactory;
import de.qaware.qav.graph.api.Constants;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.factory.DependencyGraphFactory;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link ComponentNameTagger}.
 *
 * @author QAware GmbH
 */
public class ComponentNameTaggerTest {

    @Test
    public void testTagComponentNames() {
        DependencyGraph dependencyGraph = DependencyGraphFactory.createGraph();

        dependencyGraph.getOrCreateNodeByName("X.A.b");
        dependencyGraph.getOrCreateNodeByName("X.A.c");
        dependencyGraph.getOrCreateNodeByName("X.A.d");

        dependencyGraph.getOrCreateNodeByName("com.company.project.E.f");
        dependencyGraph.getOrCreateNodeByName("com.company.project.E.g");

        // Note that this does not create architecture nodes in the dependency graph.
        Architecture packageArchitecture = new DefaultPackageArchitectureFactory(dependencyGraph).createArchitecture();

        List<String> unmappedClasses = ComponentNameTagger.tagComponentNames(dependencyGraph, packageArchitecture);

        String tag = packageArchitecture.getName() + Constants.PARENT_SUFFIX;
        assertThat(dependencyGraph.getNode("X.A.b").getProperty(tag), is("X.A"));
        assertThat(dependencyGraph.getNode("X.A.c").getProperty(tag), is("X.A"));
        assertThat(dependencyGraph.getNode("X.A.d").getProperty(tag), is("X.A"));

        assertThat(dependencyGraph.getNode("com.company.project.E.f").getProperty(tag), is("com.company.project.E"));
        assertThat(dependencyGraph.getNode("com.company.project.E.g").getProperty(tag), is("com.company.project.E"));

        assertThat(dependencyGraph.getNode("com.company.project.E"), nullValue());

        assertThat(unmappedClasses, notNullValue());
        assertThat(unmappedClasses, hasSize(0));
    }
}