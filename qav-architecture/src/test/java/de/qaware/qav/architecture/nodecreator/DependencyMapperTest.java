package de.qaware.qav.architecture.nodecreator;

import de.qaware.qav.architecture.dsl.model.Architecture;
import de.qaware.qav.architecture.factory.DefaultPackageArchitectureFactory;
import de.qaware.qav.architecture.nodecreator.impl.ArchitectureNodeCreator;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.Node;
import de.qaware.qav.graph.factory.DependencyGraphFactory;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link DependencyMapper}.
 *
 * @author QAware GmbH
 */
public class DependencyMapperTest {

    @Test
    public void testMap() {
        DependencyGraph dependencyGraph = DependencyGraphFactory.createGraph();

        Node a = dependencyGraph.getOrCreateNodeByName("X.A");
        Node b = dependencyGraph.getOrCreateNodeByName("X.A.b");
        Node c = dependencyGraph.getOrCreateNodeByName("X.A.c");
        Node d = dependencyGraph.getOrCreateNodeByName("X.A.d");

        Node e = dependencyGraph.getOrCreateNodeByName("Y.E");
        Node f = dependencyGraph.getOrCreateNodeByName("Y.E.f");
        Node g = dependencyGraph.getOrCreateNodeByName("Y.E.g");

        dependencyGraph.addDependency(b, c, DependencyType.CREATE);
        dependencyGraph.addDependency(b, d, DependencyType.CREATE);
        dependencyGraph.addDependency(c, d, DependencyType.CREATE);
        dependencyGraph.addDependency(f, g, DependencyType.CREATE);
        dependencyGraph.addDependency(b, g, DependencyType.INHERIT);

        assertThat(dependencyGraph.getEdge(a, e), nullValue());

        Architecture packageArchitecture = new DefaultPackageArchitectureFactory(dependencyGraph).createArchitecture();
        new ArchitectureNodeCreator(dependencyGraph, packageArchitecture).createAllArchitectureNodes();
        DependencyMapper.mapDependencies(dependencyGraph, packageArchitecture.getName());

        Node x = dependencyGraph.getNode("X");
        assertThat(x, notNullValue());
        Node y = dependencyGraph.getNode("Y");
        assertThat(y, notNullValue());

        assertThat(dependencyGraph.getEdge(a, e), notNullValue());
        assertThat(dependencyGraph.getEdge(a, e).getDependencyType(), is(DependencyType.INHERIT));

        assertThat(dependencyGraph.getEdge(x, y), nullValue());
    }
}