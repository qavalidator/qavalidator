package de.qaware.qav.visualization.impl;

import de.qaware.qav.architecture.dsl.model.Architecture;
import de.qaware.qav.architecture.factory.DefaultPackageArchitectureFactory;
import de.qaware.qav.architecture.viewcreator.DependencyMapper;
import de.qaware.qav.architecture.viewcreator.impl.ArchitectureNodeCreator;
import de.qaware.qav.architecture.viewcreator.impl.BaseRelationTagger;
import de.qaware.qav.graph.api.Constants;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.Node;
import de.qaware.qav.graph.factory.DependencyGraphFactory;
import org.junit.Before;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Base class to setup a graph for export tests.
 *
 * @author QAware GmbH
 */
public abstract class AbstractExporterTest {

    protected DependencyGraph dependencyGraph;
    protected Architecture architecture;

    @Before
    public void setup() {
        dependencyGraph = DependencyGraphFactory.createGraph();
        Node n1 = dependencyGraph.getOrCreateNodeByName("root.c1.n1");
        Node n2 = dependencyGraph.getOrCreateNodeByName("root.c1.n2");
        Node n3 = dependencyGraph.getOrCreateNodeByName("root.c2.n3");
        Node n4 = dependencyGraph.getOrCreateNodeByName("root.c3.n4");
        Node n5 = dependencyGraph.getOrCreateNodeByName("root.c3.n5");
        n1.setProperty(Constants.TYPE, "class");
        n2.setProperty(Constants.TYPE, "class");
        n3.setProperty(Constants.TYPE, "class");
        n4.setProperty(Constants.TYPE, "class");
        n5.setProperty(Constants.TYPE, "class");

        dependencyGraph.addDependency(n1, n2, DependencyType.READ_ONLY);
        dependencyGraph.addDependency(n1, n3, DependencyType.READ_WRITE);
        dependencyGraph.addDependency(n2, n3, DependencyType.INHERIT);
        dependencyGraph.addDependency(n2, n4, DependencyType.INHERIT);
        dependencyGraph.addDependency(n4, n5, DependencyType.CREATE);

        architecture = new DefaultPackageArchitectureFactory(dependencyGraph).createArchitecture();
        new ArchitectureNodeCreator(dependencyGraph, architecture).createAllArchitectureNodes();
        DependencyMapper.mapDependencies(dependencyGraph, architecture.getName());
        BaseRelationTagger.tagBaseRelationNumbers(dependencyGraph);

        assertThat(dependencyGraph.getNode("root.c1"), notNullValue());
    }
}
