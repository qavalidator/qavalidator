package de.qaware.qav.visualization.impl;

import de.qaware.qav.util.FileSystemUtil;
import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link DotExporter}.
 *
 * More like a smoke test so far.
 *
 * @author QAware GmbH
 */
public class DotExporterTest extends AbstractExporterTest {

    @Test
    public void exportGraphWithEdgeLabels() {
        String fileNameBase = "build/test-output/test_01";
        DotExporter dotExporter = new DotExporter(dependencyGraph, fileNameBase, architecture, new ArrayList<>(), true);
        dotExporter.exportGraph();

        String graphML = FileSystemUtil.readFileAsText(fileNameBase + ".dot");
        assertThat(graphML, notNullValue());
        assertThat(graphML, containsString("root.c1.n1"));
        assertThat(graphML, containsString("_root_c1 -> _root_c2 [ color = \"#67001f\", fontcolor = \"#67001f\", penwidth = 3.0, style = dashed , label=\"2\", headlabel=\"1\", taillabel=\"2\"];"));
    }

    @Test
    public void exportGraphWithoutEdgeLabels() {
        String fileNameBase = "build/test-output/test_02";
        DotExporter dotExporter = new DotExporter(dependencyGraph, fileNameBase, architecture, new ArrayList<>(), false);
        dotExporter.exportGraph();

        String graphML = FileSystemUtil.readFileAsText(fileNameBase + ".dot");
        assertThat(graphML, notNullValue());
        assertThat(graphML, not(containsString("_root_c1 -> _root_c2 [ color = \"#000000\", fontcolor = \"#000000\", penwidth = 3.0, style = dashed , label=\"2\", headlabel=\"1\", taillabel=\"2\"];")));
    }
}
