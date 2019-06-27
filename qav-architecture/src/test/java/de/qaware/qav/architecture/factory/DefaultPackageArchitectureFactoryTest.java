package de.qaware.qav.architecture.factory;

import com.google.common.collect.Lists;
import de.qaware.qav.architecture.dsl.model.Architecture;
import de.qaware.qav.architecture.dsl.model.ClassSet;
import de.qaware.qav.architecture.dsl.model.Component;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.factory.DependencyGraphFactory;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Tests for {@link DefaultPackageArchitectureFactory}.
 *
 * @author QAware GmbH
 */
public class DefaultPackageArchitectureFactoryTest {

    @Test
    public void testCreateArchitecture() {
        DependencyGraph dependencyGraph = DependencyGraphFactory.createGraph();

        dependencyGraph.getOrCreateNodeByName("com.example.api.A");
        dependencyGraph.getOrCreateNodeByName("com.example.api.B");
        dependencyGraph.getOrCreateNodeByName("com.example.api.C");

        DefaultPackageArchitectureFactory factory = new DefaultPackageArchitectureFactory(dependencyGraph);
        factory.setArchitectureName("my-architecture");
        Architecture architecture = factory.createArchitecture();
        assertThat(architecture.getName(), is("my-architecture"));

        assertThat(architecture.getAllComponents(), hasSize(3));
        assertThat(getComponentNameList(architecture),
                is(Lists.newArrayList("com.example.api", "com.example", "com")));

        assertThat(architecture.getParentComponentName("com.example.api.A"), is("com.example.api"));
        Component component = architecture.getApiNameToComponent().get("com.example.api");
        assertThat(component.getName(), is("com.example.api"));
        ClassSet apiDef = component.getApi().get("com.example.api");
        assertThat(apiDef.getPatterns(), hasSize(1));
        assertThat(apiDef.getPatterns().get(0), is("com.example.api.**"));
    }

    @Test
    public void testCreateArchitectureMoreComponents() {
        DependencyGraph dependencyGraph = DependencyGraphFactory.createGraph();

        dependencyGraph.getOrCreateNodeByName("com.example.api.A");
        dependencyGraph.getOrCreateNodeByName("com.example.api.B");
        dependencyGraph.getOrCreateNodeByName("com.example.impl.AImpl");

        Architecture architecture = new DefaultPackageArchitectureFactory(dependencyGraph).createArchitecture();

        assertThat(architecture.getName(), is("Package"));
        assertThat(architecture.getAllComponents(), hasSize(4));
        assertThat(getComponentNameList(architecture),
                is(Lists.newArrayList("com.example.impl", "com.example.api", "com.example", "com")));

        assertThat(architecture.getParentComponentName("com.example.api.A"), is("com.example.api"));
        assertThat(architecture.getParentComponentName("com.example.impl.AImpl"), is("com.example.impl"));
        assertThat(architecture.getParentComponentName("com.example.impl.AnotherImpl"), is("com.example.impl"));

        Component component = architecture.getApiNameToComponent().get("com.example.api");
        assertThat(component.getName(), is("com.example.api"));
        ClassSet apiDef = component.getApi().get("com.example.api");
        assertThat(apiDef.getPatterns(), hasSize(1));
        assertThat(apiDef.getPatterns().get(0), is("com.example.api.**"));

        Component impl = architecture.getApiNameToComponent().get("com.example.impl");
        assertThat(impl.getName(), is("com.example.impl"));
        ClassSet implDef = impl.getApi().get("com.example.impl");
        assertThat(implDef.getPatterns(), hasSize(1));
        assertThat(implDef.getPatterns().get(0), is("com.example.impl.**"));
    }

    @Test
    public void testCreateArchitectureNestedComponents() {
        DependencyGraph dependencyGraph = DependencyGraphFactory.createGraph();

        dependencyGraph.getOrCreateNodeByName("com.example.api.A");
        dependencyGraph.getOrCreateNodeByName("com.example.api.B");
        dependencyGraph.getOrCreateNodeByName("com.example.impl.AImpl");
        dependencyGraph.getOrCreateNodeByName("com.example.impl.a.X");
        dependencyGraph.getOrCreateNodeByName("com.example.impl.b.Y");
        dependencyGraph.getOrCreateNodeByName("org.abc.api.d.z");

        Architecture architecture = new DefaultPackageArchitectureFactory(dependencyGraph).createArchitecture();

        assertThat(architecture.getName(), is("Package"));
        assertThat(architecture.getAllComponents(), hasSize(10));
        assertThat(getComponentNameList(architecture),
                is(Lists.newArrayList("com.example.impl.a", "com.example.impl.b",
                        "com.example.impl", "com.example.api", "org.abc.api.d",
                        "com.example", "org.abc.api", "org.abc",
                        "com", "org")));

        assertThat(architecture.getParentComponentName("com.example.api.A"), is("com.example.api"));
        assertThat(architecture.getParentComponentName("com.example.impl.AImpl"), is("com.example.impl"));
        assertThat(architecture.getParentComponentName("com.example.impl.AnotherImpl"), is("com.example.impl"));
        assertThat(architecture.getParentComponentName("com.example.impl.a.Something"), is("com.example.impl.a"));
        assertThat(architecture.getParentComponentName("com.example.impl.b.Something"), is("com.example.impl.b"));
        assertThat(architecture.getParentComponentName("com.example.impl.c.Something"), is("com.example.impl"));
        assertThat(architecture.getParentComponentName("com.example.SomethingElse"), is("com.example"));

        Component component = architecture.getApiNameToComponent().get("com.example.api");
        assertThat(component.getName(), is("com.example.api"));
        ClassSet apiDef = component.getApi().get("com.example.api");
        assertThat(apiDef.getPatterns(), hasSize(1));
        assertThat(apiDef.getPatterns().get(0), is("com.example.api.**"));

        Component impl = architecture.getApiNameToComponent().get("com.example.impl.a");
        assertThat(impl.getName(), is("com.example.impl.a"));
        ClassSet implDef = impl.getApi().get("com.example.impl.a");
        assertThat(implDef.getPatterns(), hasSize(1));
        assertThat(implDef.getPatterns().get(0), is("com.example.impl.a.**"));
    }

    @Test
    public void testCreateArchitectureNestedComponentsLimitedDepth() {
        DependencyGraph dependencyGraph = DependencyGraphFactory.createGraph();

        dependencyGraph.getOrCreateNodeByName("com.example.api.A");
        dependencyGraph.getOrCreateNodeByName("com.example.api.B");
        dependencyGraph.getOrCreateNodeByName("com.example.impl.AImpl");
        dependencyGraph.getOrCreateNodeByName("com.example.impl.a.X");
        dependencyGraph.getOrCreateNodeByName("com.example.impl.b.Y");
        dependencyGraph.getOrCreateNodeByName("org.abc.api.d.z");

        Architecture architecture = new DefaultPackageArchitectureFactory(dependencyGraph).createArchitecture(3);

        assertThat(architecture.getName(), is("Package-3"));
        assertThat(architecture.getAllComponents(), hasSize(7));
        assertThat(getComponentNameList(architecture),
                is(Lists.newArrayList("com.example.impl", "com.example.api", "com.example",
                        "org.abc.api", "org.abc",
                        "com", "org")));

        assertThat(architecture.getParentComponentName("com.example.api.A"), is("com.example.api"));
        assertThat(architecture.getParentComponentName("com.example.impl.AImpl"), is("com.example.impl"));
        assertThat(architecture.getParentComponentName("com.example.impl.AnotherImpl"), is("com.example.impl"));
        assertThat(architecture.getParentComponentName("com.example.impl.a.Something"), is("com.example.impl"));
        assertThat(architecture.getParentComponentName("com.example.impl.b.Something"), is("com.example.impl"));
        assertThat(architecture.getParentComponentName("com.example.impl.c.Something"), is("com.example.impl"));
        assertThat(architecture.getParentComponentName("com.example.SomethingElse"), is("com.example"));

        Component component = architecture.getApiNameToComponent().get("com.example.api");
        assertThat(component.getName(), is("com.example.api"));
        ClassSet apiDef = component.getApi().get("com.example.api");
        assertThat(apiDef.getPatterns(), hasSize(1));
        assertThat(apiDef.getPatterns().get(0), is("com.example.api.**"));

        Component impl = architecture.getApiNameToComponent().get("com.example.impl");
        assertThat(impl.getName(), is("com.example.impl"));
        ClassSet implDef = impl.getApi().get("com.example.impl");
        assertThat(implDef.getPatterns(), hasSize(1));
        assertThat(implDef.getPatterns().get(0), is("com.example.impl.**"));
    }

    @Test
    public void testCreateArchitectureEmptyGraph() {
        DependencyGraph dependencyGraph = DependencyGraphFactory.createGraph();

        Architecture architecture = new DefaultPackageArchitectureFactory(dependencyGraph).createArchitecture();

        assertThat(architecture.getName(), is("Package"));
        assertThat(architecture.getAllComponents(), hasSize(0));
    }

    @Test
    public void testCreateArchitectureOnePackage() {
        DependencyGraph dependencyGraph = DependencyGraphFactory.createGraph();
        dependencyGraph.getOrCreateNodeByName("ClassInDefaultPackage");

        Architecture architecture = new DefaultPackageArchitectureFactory(dependencyGraph).createArchitecture();

        assertThat(architecture.getName(), is("Package"));

        assertThat(architecture.getAllComponents(), hasSize(1));
        assertThat(getComponentNameList(architecture),
                is(Lists.newArrayList("ClassInDefaultPackage")));

        assertThat(architecture.getParentComponent("ClassInDefaultPackage"), is(architecture));
        assertThat(architecture.getParentComponent("anythingElse"), nullValue());
    }

    @Test
    public void testCreateArchitectureTwoPackages() {
        DependencyGraph dependencyGraph = DependencyGraphFactory.createGraph();
        dependencyGraph.getOrCreateNodeByName("org.a");

        Architecture architecture = new DefaultPackageArchitectureFactory(dependencyGraph).createArchitecture();

        assertThat(architecture.getName(), is("Package"));

        assertThat(architecture.getAllComponents(), hasSize(1));
        assertThat(getComponentNameList(architecture),
                is(Lists.newArrayList("org")));

        assertThat(architecture.getParentComponent("org.a.A").getName(), is("org")); // "org.a" is a leaf!
        assertThat(architecture.getParentComponent("org.a").getName(), is("org"));
        assertThat(architecture.getParentComponent("org"), is(architecture));
        assertThat(architecture.getParentComponent("anythingElse"), nullValue());
    }

    @Test(expected = NullPointerException.class)
    public void testNullInput() {
        new DefaultPackageArchitectureFactory(null);
    }

    private List<String> getComponentNameList(Architecture architecture) {
        return architecture.getAllComponents().stream().map(Component::getName).collect(Collectors.toList());
    }

    //
    // --- getComponentName()
    //

    @Test
    public void testGetComponentName() {
        DependencyGraph dependencyGraph = DependencyGraphFactory.createGraph();
        DefaultPackageArchitectureFactory factory = new DefaultPackageArchitectureFactory(dependencyGraph);

        assertThat(factory.getComponentName("com.example.api.A", 0), is("com.example.api"));
        assertThat(factory.getComponentName("com.example.api", 0), is("com.example"));
        assertThat(factory.getComponentName("com.example", 0), is("com"));
        assertThat(factory.getComponentName("com", 0), nullValue());
        assertThat(factory.getComponentName("", 0), nullValue());

        try {
            factory.getComponentName(null, 0);
            fail("Expected NPE");
        } catch(NullPointerException e) {
            assertThat(e.getMessage(), is("name"));
        }
    }

    @Test
    public void testGetComponentNameWithMaxLength() {
        DependencyGraph dependencyGraph = DependencyGraphFactory.createGraph();
        DefaultPackageArchitectureFactory factory = new DefaultPackageArchitectureFactory(dependencyGraph);

        assertThat(factory.getComponentName("com.example.app.web.api.A", 4), is("com.example.app.web"));
        assertThat(factory.getComponentName("com.example.app.biz.api.B", 4), is("com.example.app.biz"));
        assertThat(factory.getComponentName("com.example.app.db.api.C", 4), is("com.example.app.db"));

        assertThat(factory.getComponentName("com.example.api.A", 4), is("com.example.api"));
        assertThat(factory.getComponentName("com.example.api", 4), is("com.example"));
        assertThat(factory.getComponentName("com.example", 4), is("com"));
        assertThat(factory.getComponentName("com", 4), nullValue());
        assertThat(factory.getComponentName("", 4), nullValue());

        try {
            factory.getComponentName(null, 4);
            fail("Expected NPE");
        } catch(NullPointerException e) {
            assertThat(e.getMessage(), is("name"));
        }
    }

    @Test
    public void testGetComponentNameWithOtherRegex() {
        DependencyGraph dependencyGraph = DependencyGraphFactory.createGraph();
        DefaultPackageArchitectureFactory factory = new DefaultPackageArchitectureFactory(dependencyGraph);
        factory.setPathSeparator(":");

        assertThat(factory.getComponentName("com.example.app:app-api", 0), is("com.example.app"));
    }

    @Test
    public void testGetComponentNameWithOtherRegex2() {
        DependencyGraph dependencyGraph = DependencyGraphFactory.createGraph();
        DefaultPackageArchitectureFactory factory = new DefaultPackageArchitectureFactory(dependencyGraph);
        factory.setPathSeparator("#");

        assertThat(factory.getComponentName("com.example.app#Class.ts#methodA", 0), is("com.example.app#Class.ts"));
        assertThat(factory.getComponentName("com.example.app#Class.ts", 0), is("com.example.app"));
        assertThat(factory.getComponentName("com.example.app", 0), nullValue());
    }

    @Test
    public void testFactoryWithOtherRegex() {
        DependencyGraph dependencyGraph = DependencyGraphFactory.createGraph();
        dependencyGraph.getOrCreateNodeByName("com.example.app:app-api");
        DefaultPackageArchitectureFactory factory = new DefaultPackageArchitectureFactory(dependencyGraph);
        factory.setPathSeparator(":");

        assertThat(factory.getComponentName("com.example.app:app-api", 0), is("com.example.app"));

        Architecture architecture = factory.createArchitecture();
        Component component = architecture.getParentComponent("com.example.app:app-api");
        assertThat(component, notNullValue());
        assertThat(component.getName(), is("com.example.app"));
    }
}
