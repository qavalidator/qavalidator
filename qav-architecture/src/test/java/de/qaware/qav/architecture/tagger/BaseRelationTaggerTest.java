package de.qaware.qav.architecture.tagger;

import de.qaware.qav.architecture.factory.DefaultPackageArchitectureFactory;
import de.qaware.qav.architecture.dsl.model.Architecture;
import de.qaware.qav.architecture.nodecreator.ArchitectureNodeCreator;
import de.qaware.qav.architecture.nodecreator.DependencyMapper;
import de.qaware.qav.graph.api.Constants;
import de.qaware.qav.graph.api.Dependency;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.Node;
import de.qaware.qav.graph.factory.DependencyGraphFactory;
import de.qaware.qav.graph.filter.NodePropertyInFilter;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link BaseRelationTagger}.
 *
 * @author QAware GmbH
 */
public class BaseRelationTaggerTest {

    private DependencyGraph dependencyGraph;

    @Before
    public void setup() {
        dependencyGraph = DependencyGraphFactory.createGraph();

        Node a = dependencyGraph.getOrCreateNodeByName("com.company.project.api.A");
        Node b = dependencyGraph.getOrCreateNodeByName("com.company.project.api.B");
        Node aImpl = dependencyGraph.getOrCreateNodeByName("com.company.project.impl.comp1.AImpl");
        Node bImpl = dependencyGraph.getOrCreateNodeByName("com.company.project.impl.comp1.BImpl");
        Node c = dependencyGraph.getOrCreateNodeByName("com.company.project.impl.comp2.C");
        Node d = dependencyGraph.getOrCreateNodeByName("com.company.project.impl.comp2.D");
        Node e = dependencyGraph.getOrCreateNodeByName("com.company.project.impl.util.E");
        Node f = dependencyGraph.getOrCreateNodeByName("com.company.project.impl.util.F");
        Node g = dependencyGraph.getOrCreateNodeByName("com.company.project.impl.util.G");

        dependencyGraph.addDependency(aImpl, a, DependencyType.INHERIT);
        dependencyGraph.addDependency(bImpl, b, DependencyType.INHERIT);
        dependencyGraph.addDependency(aImpl, c, DependencyType.READ_WRITE);
        dependencyGraph.addDependency(bImpl, d, DependencyType.READ_WRITE);
        dependencyGraph.addDependency(c, e, DependencyType.READ_WRITE);
        dependencyGraph.addDependency(d, f, DependencyType.READ_WRITE);
        dependencyGraph.addDependency(d, g, DependencyType.READ_ONLY);

        // Note that the package architecture does not include the leaves at all, so the packages start with height "0".
        Architecture packageArchitecture = new DefaultPackageArchitectureFactory(dependencyGraph).createArchitecture();
        ArchitectureNodeCreator.createAllArchitectureNodes(dependencyGraph, packageArchitecture);
        DependencyMapper.mapDependencies(dependencyGraph, packageArchitecture.getName());
    }

    @Test
    public void testBaseRelationTagger() {
        DependencyGraph packageGraph = dependencyGraph.filter(new NodePropertyInFilter("architecture", "Package"));

        assertThat(packageGraph.getAllNodes(), hasSize(9));
        Node api = assertNode(packageGraph, "com.company.project.api");
        Node comp1 = assertNode(packageGraph, "com.company.project.impl.comp1");
        Node comp2 = assertNode(packageGraph, "com.company.project.impl.comp2");
        Node util = assertNode(packageGraph, "com.company.project.impl.util");

        BaseRelationTagger.tagBaseRelationNumbers(packageGraph);

        Dependency d1 = assertEdge(packageGraph, comp1, api);
        assertThat(d1.getProperty(Constants.BASE_REL_COUNT), is(2));
        assertThat(d1.getProperty(Constants.BASE_REL_COUNT_SOURCES), is(2));
        assertThat(d1.getProperty(Constants.BASE_REL_COUNT_TARGETS), is(2));

        Dependency d2 = assertEdge(packageGraph, comp2, util);
        assertThat(d2.getProperty(Constants.BASE_REL_COUNT), is(3));
        assertThat(d2.getProperty(Constants.BASE_REL_COUNT_SOURCES), is(2));
        assertThat(d2.getProperty(Constants.BASE_REL_COUNT_TARGETS), is(3));

        Node com = assertNode(packageGraph, "com");
        Node company = assertNode(packageGraph, "com.company");
        Dependency d3 = assertEdge(packageGraph, com, company);
        assertThat(d3.hasProperty(Constants.BASE_REL_COUNT), is(false));
    }

    private static Node assertNode(DependencyGraph graph, String name) {
        Node result = graph.getNode(name);
        assertThat(result, notNullValue());
        return result;
    }

    private static Dependency assertEdge(DependencyGraph graph, Node from, Node to) {
        Dependency result = graph.getEdge(from, to);
        assertThat(result, notNullValue());
        return result;
    }
}