package de.qaware.qav.graph.io;

import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.util.FileSystemUtil;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author QAware GmbH
 */
public class NodePrinterTest {

    @Test
    public void printNodes()  {
        DependencyGraph graph = GraphReaderWriter.read("src/test/resources/graphs/testGraph1.json");
        new NodePrinter(graph, "build/nodes.txt").printNodes();

        String written = FileSystemUtil.readFileAsText("build/nodes.txt").trim().replaceAll("\\r\\n", "\n");
        String expected = FileSystemUtil.readFileAsText("src/test/resources/graphs/expected_nodes1.txt").trim().replaceAll("\\r\\n", "\n");

        assertThat(written, is(expected));
    }

}