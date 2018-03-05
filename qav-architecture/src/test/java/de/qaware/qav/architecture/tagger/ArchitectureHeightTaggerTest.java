package de.qaware.qav.architecture.tagger;

import de.qaware.qav.architecture.dsl.model.Architecture;
import de.qaware.qav.architecture.factory.DefaultPackageArchitectureFactory;
import de.qaware.qav.architecture.nodecreator.impl.ArchitectureNodeCreator;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.factory.DependencyGraphFactory;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link ArchitectureHeightTagger}.
 *
 * @author QAware GmbH
 */
public class ArchitectureHeightTaggerTest {

    @Test
    public void testTagArchitectureHeight() {
        DependencyGraph dependencyGraph = DependencyGraphFactory.createGraph();

        dependencyGraph.getOrCreateNodeByName("X.A.b");
        dependencyGraph.getOrCreateNodeByName("X.A.c");
        dependencyGraph.getOrCreateNodeByName("X.A.d");

        dependencyGraph.getOrCreateNodeByName("com.company.project.E.f");
        dependencyGraph.getOrCreateNodeByName("com.company.project.E.g");

        // Note that the package architecture does not include the leaves at all, so the packages start with height "0".
        Architecture packageArchitecture = new DefaultPackageArchitectureFactory(dependencyGraph).createArchitecture();
        new ArchitectureNodeCreator(dependencyGraph, packageArchitecture).createAllArchitectureNodes();
        ArchitectureHeightTagger.tagArchitectureHeight(dependencyGraph, packageArchitecture);

        String key = packageArchitecture.getName() + "-height";

        assertThat(dependencyGraph.getNode("com.company.project.E"), notNullValue());
        assertThat(dependencyGraph.getNode("com.company.project.E").getProperty(key), is(0));

        assertThat(dependencyGraph.getNode("com.company.project.E.f").getProperty(key), nullValue());
        assertThat(dependencyGraph.getNode("com.company.project.E.g").getProperty(key), nullValue());

        assertThat(dependencyGraph.getNode("com.company.project.E").getProperty(key), is(0));
        assertThat(dependencyGraph.getNode("com.company.project").getProperty(key), is(1));
        assertThat(dependencyGraph.getNode("com.company").getProperty(key), is(2));
        assertThat(dependencyGraph.getNode("com").getProperty(key), is(3));
    }
}