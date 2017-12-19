package de.qaware.qav.input.javacode;

import de.qaware.qav.graph.api.Dependency;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.Node;
import de.qaware.qav.graph.factory.DependencyGraphFactory;
import de.qaware.qav.util.FileSystemUtil;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Test for {@link DependencyReader}.
 *
 * @author QAware GmbH
 */
public class DependencyReaderTest {

    public static final String TEST_CLASSES_ROOT = "build/classes/java/test";

    private DependencyGraph dependencyGraph;
    private DependencyReader dependencyReader;

    @Before
    public void setup() {
        dependencyGraph = DependencyGraphFactory.createGraph();
        dependencyReader = new DependencyReader(dependencyGraph, false);
    }

    @Test
    public void testFindDependencies() {
        String className = DependencyReaderTest.class.getCanonicalName();
        String fileName = TEST_CLASSES_ROOT + "/" + className.replaceAll("\\.", "/") + ".class";

        byte[] bytesFromFile = FileSystemUtil.readBytesFromFile(fileName);
        dependencyReader.readDependencies(bytesFromFile);

        assertThat(dependencyGraph.hasNode(className), is(true));
        assertThat(dependencyGraph.hasNode(DependencyGraphFactory.class.getName()), is(true));
        Node from = dependencyGraph.getNode(className);
        Node to = dependencyGraph.getNode(DependencyGraphFactory.class.getName());
        assertThat(dependencyGraph.getIncomingEdges(to).size(), is(1));

        assertThat(dependencyGraph.hasNode("java.lang.Object"), is(false)); // we excluded it.

        Dependency dependency = dependencyGraph.getEdge(from, to);
        assertThat(dependency, notNullValue());
        assertThat(dependency.getDependencyType(), is(DependencyType.READ_WRITE));
    }

    @Test
    public void testReadWrongFile() {
        try {
            dependencyReader.readDependencies(null);
            fail("reading not existing file should fail");
        } catch(NullPointerException e) {
            assertThat(e.getMessage(), is("Class could not be read"));
        }
    }

}