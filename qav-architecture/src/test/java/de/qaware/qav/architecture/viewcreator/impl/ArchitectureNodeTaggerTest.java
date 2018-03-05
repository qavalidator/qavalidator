package de.qaware.qav.architecture.viewcreator.impl;

import de.qaware.qav.architecture.dsl.model.Architecture;
import de.qaware.qav.architecture.factory.DefaultPackageArchitectureFactory;
import de.qaware.qav.graph.api.Constants;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.Node;
import de.qaware.qav.graph.factory.DependencyGraphFactory;
import de.qaware.qav.graph.filter.NodePropertyInFilter;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link ArchitectureNodeTagger} and {@link ArchitectureNodeCreator}.
 *
 * @author QAware GmbH
 */
public class ArchitectureNodeTaggerTest {

    private DependencyGraph dependencyGraph;

    @Before
    public void init() {
        dependencyGraph = DependencyGraphFactory.createGraph();

        dependencyGraph.getOrCreateNodeByName("X.A");
        Node b = dependencyGraph.getOrCreateNodeByName("X.A.b");
        Node c = dependencyGraph.getOrCreateNodeByName("X.A.c");
        Node d = dependencyGraph.getOrCreateNodeByName("X.A.d");

        b.setProperty(Constants.SCOPE, "input");
        c.setProperty(Constants.SCOPE, "input");
        d.setProperty(Constants.SCOPE, "input");

        dependencyGraph.getOrCreateNodeByName("Y.E");
        dependencyGraph.getOrCreateNodeByName("Y.E.f");
        dependencyGraph.getOrCreateNodeByName("Y.E.g");
    }

    @Test
    public void testTagArchitectureNodes() {
        Architecture packageArchitecture = new DefaultPackageArchitectureFactory(dependencyGraph).createArchitecture();

        DependencyGraph inputGraph = dependencyGraph.filter(new NodePropertyInFilter("scope", "input"));

        new ArchitectureNodeCreator(dependencyGraph, packageArchitecture).createAllArchitectureNodes();
        ArchitectureNodeTagger.tagArchitectureNodes(inputGraph, packageArchitecture, "my-tag");

        Node x = assertNode("X");
        assertThat(x.getProperty("my-tag"), notNullValue());
        assertThat(x.getProperty("my-tag"), is(true));

        Node y = assertNode("Y");
        assertThat(y.getProperty("my-tag"), nullValue());
    }

    private Node assertNode(String name) {
        Node result = dependencyGraph.getNode(name);
        assertThat(result, notNullValue());
        return result;
    }
}