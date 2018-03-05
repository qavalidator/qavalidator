package de.qaware.qav.architecture.checker;

import de.qaware.qav.architecture.dsl.api.QavArchitectureReader;
import de.qaware.qav.architecture.dsl.model.Architecture;
import de.qaware.qav.architecture.nodecreator.impl.ArchitectureNodeCreator;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.Node;
import de.qaware.qav.graph.factory.DependencyGraphFactory;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

/**
 * Tests for {@link DependencyChecker}.
 *
 * @author QAware GmbH
 */
public class DependencyCheckerTest extends AbstractArchitectureCheckerTest {

    @Test
    public void testCheckWithMatchingRule() {
        init(false, false);
        DependencyChecker checker = new DependencyChecker(architectureGraph, packageArchitecture);
        assertThat(checker.isOk(), is(true));
        assertThat(checker.getViolationMessages(), hasSize(0));
        assertThat(checker.getViolationMessage(), nullValue());
    }

    @Test
    public void testCheckWithoutMatchingRule() {
        init(true, false);
        DependencyChecker checker = new DependencyChecker(architectureGraph, packageArchitecture);
        assertThat(checker.isOk(), is(false));
        assertThat(checker.getViolationMessages(), hasSize(1));
        assertThat(checker.getViolationMessages().get(0), is("com.my.a.b --[READ_ONLY]--> org.other"));
        assertThat(checker.getViolationMessage(), is("1 uncovered dependencies: [com.my.a.b --[READ_ONLY]--> org.other]"));
    }

    @Test
    public void testCheckWithMatchingParentRule() {
        DependencyGraph graph = DependencyGraphFactory.createGraph();

        Node n1 = graph.getOrCreateNodeByName("com.my.a.api.XxDto");
        Node n2 = graph.getOrCreateNodeByName("com.my.a.impl.XxServiceImpl");
        Node n3 = graph.getOrCreateNodeByName("com.google.commons.SuperUtil");
        graph.addDependency(n1, n2, DependencyType.READ_WRITE);
        graph.addDependency(n2, n3, DependencyType.READ_ONLY);

        QavArchitectureReader reader = new QavArchitectureReader("classpath:/qa/arch-for-dependency-checker-test.groovy", null);
        reader.read();
        Architecture architecture = reader.getArchitectures().get("Test-1");
        assertThat(architecture, notNullValue());
        new ArchitectureNodeCreator(graph, architecture).createAllArchitectureNodes();

        DependencyChecker checker = new DependencyChecker(graph, architecture);
        assertThat(checker.isOk(), is(true));
        assertThat(checker.getViolationMessages(), hasSize(0));
        assertThat(checker.getViolationMessage(), nullValue());
    }
}
