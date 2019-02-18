package de.qaware.qav.maven;

import com.google.common.collect.Lists;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.Node;
import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Tests for {@link MavenDependencyFinder}.
 */
public class MavenDependencyFinderTest {

    private MavenProject project;

    @Before
    public void setup() {
        project = new MavenProject();
        project.setGroupId("de.qaware.qav");
        project.setArtifactId("qav-module-1");
        Dependency dep1 = new Dependency();
        dep1.setGroupId("g1");
        dep1.setArtifactId("a1");
        dep1.setScope("compile");
        project.setDependencies(Lists.newArrayList(dep1));

        MavenProject parent = new MavenProject();
        parent.setGroupId("de.qaware.qav");
        parent.setArtifactId("parent");
        project.setParent(parent);

        MavenProject child1 = new MavenProject();
        child1.setGroupId("de.qaware.qav");
        child1.setArtifactId("child1");
        Dependency dep2 = new Dependency();
        dep2.setGroupId("g2");
        dep2.setArtifactId("a2");
        dep2.setScope("test");
        Dependency dep3 = new Dependency();
        dep3.setGroupId("g3");
        dep3.setArtifactId("a3");
        dep3.setScope("runtime");
        child1.setDependencies(Lists.newArrayList(dep2, dep3));
        child1.setCollectedProjects(new ArrayList<>());

        project.setCollectedProjects(Lists.newArrayList(child1));
    }

    @Test
    public void testDependencyFinder() {
        MavenDependencyFinder mavenDependencyFinder = new MavenDependencyFinder();
        mavenDependencyFinder.findDependencies(this.project);
        DependencyGraph graph = mavenDependencyFinder.getDependencyGraph();

        assertThat(graph.getAllNodes(), hasSize(6));
        Node parent = graph.getNode("de.qaware.qav:parent");
        Node root = graph.getNode("de.qaware.qav:qav-module-1");
        Node child = graph.getNode("de.qaware.qav:child1");
        Node n1 = graph.getNode("g1:a1");
        Node n2 = graph.getNode("g2:a2");
        Node n3 = graph.getNode("g3:a3");
        assertThat(parent, notNullValue());
        assertThat(root, notNullValue());
        assertThat(child, notNullValue());
        assertThat(n1, notNullValue());
        assertThat(n2, notNullValue());
        assertThat(n3, notNullValue());

        assertThat(graph.getEdge(root, parent).getDependencyType(), is(DependencyType.INHERIT));
        assertThat(graph.getEdge(root, child).getDependencyType(), is(DependencyType.CONTAINS));
        assertThat(graph.getEdge(root, n1).getDependencyType(), is(DependencyType.COMPILE));
        assertThat(graph.getEdge(child, n2).getDependencyType(), is(DependencyType.TEST));
        assertThat(graph.getEdge(child, n3).getDependencyType(), is(DependencyType.RUNTIME));
    }
}