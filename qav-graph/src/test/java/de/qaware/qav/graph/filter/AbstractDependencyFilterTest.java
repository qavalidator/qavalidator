package de.qaware.qav.graph.filter;

import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.Node;
import de.qaware.qav.graph.factory.DependencyGraphFactory;
import org.junit.Before;

/**
 * @author QAware GmbH
 */
public abstract class AbstractDependencyFilterTest {

    protected DependencyGraph dependencyGraph;
    protected Node a, b, c, d, e, f, g, h, i, k, m;

    @Before
    public void init() {
        dependencyGraph = DependencyGraphFactory.createGraph();

        a = dependencyGraph.getOrCreateNodeByName("de.qaware.qav.A");
        b = dependencyGraph.getOrCreateNodeByName("de.qaware.qav.B");
        c = dependencyGraph.getOrCreateNodeByName("de.qaware.qav.C");
        d = dependencyGraph.getOrCreateNodeByName("de.qaware.qav.D");

        e = dependencyGraph.getOrCreateNodeByName("java.util.Date");
        f = dependencyGraph.getOrCreateNodeByName("java.time.LocalDateTime");
        g = dependencyGraph.getOrCreateNodeByName("java.util.List");

        h = dependencyGraph.getOrCreateNodeByName("org.hibernate.A");
        i = dependencyGraph.getOrCreateNodeByName("org.hibernate.b.C");
        k = dependencyGraph.getOrCreateNodeByName("org.hibernate.d.E");
        m = dependencyGraph.getOrCreateNodeByName("org.hibernate.F");

        dependencyGraph.addDependency(a, b, DependencyType.CREATE);
        dependencyGraph.addDependency(a, c, DependencyType.READ_ONLY);
        dependencyGraph.addDependency(a, d, DependencyType.READ_WRITE);

        dependencyGraph.addDependency(a, e, DependencyType.READ_WRITE);
        dependencyGraph.addDependency(b, f, DependencyType.READ_WRITE);
        dependencyGraph.addDependency(c, g, DependencyType.READ_WRITE);

        dependencyGraph.addDependency(a, h, DependencyType.READ_WRITE);
        dependencyGraph.addDependency(a, i, DependencyType.READ_WRITE);
        dependencyGraph.addDependency(c, k, DependencyType.READ_WRITE);
        dependencyGraph.addDependency(c, m, DependencyType.READ_WRITE);
    }
}
