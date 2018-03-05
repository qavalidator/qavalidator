package de.qaware.qav.architecture.checker;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.qaware.qav.architecture.dsl.model.Architecture;
import de.qaware.qav.architecture.dsl.model.ClassSet;
import de.qaware.qav.architecture.dsl.model.Component;
import de.qaware.qav.architecture.factory.DefaultPackageArchitectureFactory;
import de.qaware.qav.architecture.nodecreator.DependencyMapper;
import de.qaware.qav.architecture.nodecreator.impl.ArchitectureNodeCreator;
import de.qaware.qav.graph.api.Dependency;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.Node;
import de.qaware.qav.graph.factory.DependencyGraphFactory;
import de.qaware.qav.graph.filter.NodePropertyInFilter;

import java.util.HashMap;

/**
 * Base class for architecture rule checkers.
 * <p>
 * Offers the creation of a dependency graph in different constellations.
 *
 * @author QAware GmbH
 */
abstract public class AbstractArchitectureCheckerTest {

    protected DependencyGraph graph;
    protected DependencyGraph architectureGraph;
    protected Architecture packageArchitecture;

    protected void init(boolean withDisallowedDependency, boolean withUnimplementedComponent) {
        this.graph = DependencyGraphFactory.createGraph();

        Node c = graph.getOrCreateNodeByName("com.my.a.b.c");
        Node f = graph.getOrCreateNodeByName("com.my.a.e.f");
        Node g = graph.getOrCreateNodeByName("org.other.g");
        graph.addDependency(c, f, DependencyType.READ_WRITE);

        if (withDisallowedDependency) {
            graph.addDependency(c, g, DependencyType.READ_ONLY);
        }

        packageArchitecture = new DefaultPackageArchitectureFactory(graph).createArchitecture();
        new ArchitectureNodeCreator(graph, this.packageArchitecture).createAllArchitectureNodes();
        DependencyMapper.mapDependencies(graph, packageArchitecture.getName());

        if (withUnimplementedComponent) {
            Component component = new Component();
            component.setName("Another");
            HashMap<String, ClassSet> api = Maps.newHashMap();
            api.put("Another", new ClassSet("x", Lists.newArrayList("org.something.*")));
            component.setApi(api);
            packageArchitecture.getChildren().add(component);
            packageArchitecture.getAllComponents().add(component);

            Node cmpNode = graph.getOrCreateNodeByName(component.getName());
            cmpNode.setProperty("Package", true); // so that it's in the Architecture graph.
            Node architectureRoot = graph.getNode(packageArchitecture.getName());
            assert architectureRoot != null;
            graph.addDependency(architectureRoot, cmpNode, DependencyType.CONTAINS);
        }

        this.architectureGraph = graph.filter(new NodePropertyInFilter("Package", true));

        Node b = architectureGraph.getNode("com.my.a.b");
        Node e = architectureGraph.getNode("com.my.a.e");
        assert b != null;
        assert e != null;
        assert architectureGraph.getNode("com.my.a.b.c") == null;

        Dependency edge = architectureGraph.getEdge(b, e);
        assert edge != null;
    }
}
