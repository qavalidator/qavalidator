package de.qaware.qav.graphdb.persistence;

import com.google.common.collect.Lists;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.Node;
import de.qaware.qav.graph.factory.DependencyGraphFactory;
import de.qaware.qav.graphdb.model.AbstractNode;
import de.qaware.qav.graphdb.model.ArchitectureNode;
import de.qaware.qav.graphdb.model.ClassNode;
import de.qaware.qav.graphdb.model.MethodNode;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link GraphMapper}
 */
public class GraphMapperTest {

    @Test
    public void testMapProperties() {
        DependencyGraph graph = DependencyGraphFactory.createGraph();

        Node n1 = graph.getOrCreateNodeByName("n1");
        n1.setProperty("a", "aString");
        n1.setProperty("b", 1);
        n1.setProperty("c", 2L);
        n1.setProperty("d", true);
        n1.setProperty("e", Lists.newArrayList("s1", "s2"));
        n1.setProperty("f", Lists.newArrayList(1, 2L));
        n1.setProperty("g", 3.1415);

        GraphMapper graphMapper = new GraphMapper();
        graphMapper.toNeo4j(graph);

        assertThat(graphMapper.getNodes()).hasSize(1);
        AbstractNode node = graphMapper.getNodes().iterator().next();

        assertThat(node.getProperties()).hasSize(7);
        assertThat(node.getProperty("b")).isInstanceOf(Long.class); // was Integer
    }

    @Test
    public void testMapGraph() {
        // prepare
        DependencyGraph graph = DependencyGraphFactory.createGraph();

        Node a1 = graph.getOrCreateNodeByName("a1");
        a1.setProperty("type", "architecture");
        a1.setProperty("architecture", "Package");

        Node a2 = graph.getOrCreateNodeByName("a2");
        a2.setProperty("type", "architecture");
        a2.setProperty("architecture", "Package");

        Node c1 = graph.getOrCreateNodeByName("c1");
        c1.setProperty("type", "class");

        Node c2 = graph.getOrCreateNodeByName("c2");
        c2.setProperty("type", "class");

        Node m1 = graph.getOrCreateNodeByName("m1");
        m1.setProperty("type", "method");

        Node m2 = graph.getOrCreateNodeByName("m2");
        m2.setProperty("type", "method");

        Node m3 = graph.getOrCreateNodeByName("m3");
        m3.setProperty("type", "method");

        graph.addDependency(c1, c2, DependencyType.READ_WRITE);
        graph.addDependency(a1, c1, DependencyType.CONTAINS);
        graph.addDependency(a1, c2, DependencyType.CONTAINS);
        graph.addDependency(a2, a1, DependencyType.CONTAINS);

        graph.addDependency(c1, m1, DependencyType.CONTAINS);
        graph.addDependency(c1, m2, DependencyType.CONTAINS);
        graph.addDependency(c2, m3, DependencyType.CONTAINS);

        graph.addDependency(m1, m2, DependencyType.READ_ONLY);
        graph.addDependency(m1, m3, DependencyType.READ_WRITE);

        // do
        GraphMapper graphMapper = new GraphMapper();
        graphMapper.toNeo4j(graph);

        // check
        assertThat(graphMapper.getNodes()).hasSize(7);
        assertThat(graphMapper.getReferencesRelations()).hasSize(3);

        Map<String, AbstractNode> nodeMap = new HashMap<>();
        graphMapper.getNodes().forEach(node -> nodeMap.put(node.getName(), node));

        AbstractNode aa1 = nodeMap.get("a1");
        AbstractNode aa2 = nodeMap.get("a2");
        AbstractNode cc1 = nodeMap.get("c1");
        AbstractNode cc2 = nodeMap.get("c2");
        AbstractNode mm1 = nodeMap.get("m1");
        AbstractNode mm2 = nodeMap.get("m2");
        AbstractNode mm3 = nodeMap.get("m3");

        assertThat(aa1).isInstanceOf(ArchitectureNode.class);
        assertThat(cc1).isInstanceOf(ClassNode.class);
        assertThat(cc2).isInstanceOf(ClassNode.class);
        assertThat(mm1).isInstanceOf(MethodNode.class);
        assertThat(mm2).isInstanceOf(MethodNode.class);
        assertThat(mm3).isInstanceOf(MethodNode.class);

        assertThat(((ClassNode) cc1).getImplementationFor()).contains((ArchitectureNode) aa1);
        assertThat(((ArchitectureNode) aa1).getImplementations()).contains((ClassNode) cc1);
        assertThat(((ArchitectureNode) aa2).getChildren()).contains((ArchitectureNode) aa1);

        assertThat(((MethodNode) mm1).getImplementedIn().getName()).isEqualTo("c1");
        assertThat(((MethodNode) mm2).getImplementedIn().getName()).isEqualTo("c1");
        assertThat(((MethodNode) mm3).getImplementedIn().getName()).isEqualTo("c2");

        assertThat(((ClassNode) cc1).getMethods()).hasSize(2);
        assertThat(((ClassNode) cc2).getMethods()).hasSize(1);

        assertThat(mm1.getReferencesRelations()).hasSize(2);
        assertThat(mm2.getReferencesRelations()).hasSize(0);
        assertThat(mm3.getReferencesRelations()).hasSize(0);
    }
}