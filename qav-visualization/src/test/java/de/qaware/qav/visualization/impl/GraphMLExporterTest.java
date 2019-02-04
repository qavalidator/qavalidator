package de.qaware.qav.visualization.impl;

import de.qaware.qav.util.FileSystemUtil;
import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link GraphMLExporter}.
 *
 * More like a smoke test so far.
 *
 * @author QAware GmbH
 */
public class GraphMLExporterTest extends AbstractExporterTest {

    @Test
    public void exportGraphWithEdgeLabels() {
        String fileNameBase = "build/test-output/test_01";
        GraphMLExporter graphMLExporter = new GraphMLExporter(dependencyGraph, fileNameBase, architecture, new ArrayList<>(), true);
        graphMLExporter.exportGraph();

        String graphML = FileSystemUtil.readFileAsText(fileNameBase + ".graphml");
        assertThat(graphML, notNullValue());
        assertThat(graphML, containsString("root.c1.n1"));
        assertThat(graphML, containsString("<y:EdgeLabel"));
    }

    @Test
    public void exportGraphWithoutEdgeLabels() {
        String fileNameBase = "build/test-output/test_02";
        GraphMLExporter graphMLExporter = new GraphMLExporter(dependencyGraph, fileNameBase, architecture, new ArrayList<>(), false);
        graphMLExporter.exportGraph();

        String graphML = FileSystemUtil.readFileAsText(fileNameBase + ".graphml");
        assertThat(graphML, notNullValue());
        assertThat(graphML, not(containsString("<y:EdgeLabel")));
    }

}