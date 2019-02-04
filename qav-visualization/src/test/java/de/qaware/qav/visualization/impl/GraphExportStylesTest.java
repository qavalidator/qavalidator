package de.qaware.qav.visualization.impl;

import com.google.common.collect.Lists;
import de.qaware.qav.graph.api.Dependency;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.Node;
import de.qaware.qav.visualization.model.Abbreviation;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link GraphExportStyles}.
 *
 * @author QAware GmbH
 */
public class GraphExportStylesTest {

    @Test
    public void testGetEdgeStyle() {
        Dependency dep = new Dependency(new Node("n1"), new Node("n2"), DependencyType.CREATE);
        EdgeStyle edgeStyle = GraphExportStyles.getEdgeStyle(dep);
        assertThat(edgeStyle, notNullValue());
        assertThat(edgeStyle.getColor(), is("#b2182b"));
        assertThat(edgeStyle.getWidth(), is("3.0"));
        assertThat(edgeStyle.getLineStyle(), is("line"));
    }

    @Test
    public void testGetEdgeStyleDefault() {
        Dependency dep = new Dependency(new Node("n1"), new Node("n2"), DependencyType.CONTAINS);
        EdgeStyle edgeStyle = GraphExportStyles.getEdgeStyle(dep);
        assertThat(edgeStyle, notNullValue());
        assertThat(edgeStyle.getColor(), is("#67001f"));
        assertThat(edgeStyle.getWidth(), is("1.0"));
        assertThat(edgeStyle.getLineStyle(), is("line"));
    }

    @Test
    public void testGetId() {
        assertThat(GraphExportStyles.getId("com.my.a.B"), is("_com_my_a_B"));
        assertThat(GraphExportStyles.getId("com.my.a.B$Inner"), is("_com_my_a_B_Inner"));
        assertThat(GraphExportStyles.getId("<com.my.a.B$Inner>"), is("__com_my_a_B_Inner_"));
        assertThat(GraphExportStyles.getId("com.my.a.B  Inner"), is("_com_my_a_B_Inner"));
        assertThat(GraphExportStyles.getId("com.my:module"), is("_com_my_module"));
        assertThat(GraphExportStyles.getId("com.my:my-module"), is("_com_my_my_module"));
        assertThat(GraphExportStyles.getId("com/my/my-module"), is("_com_my_my_module"));
        assertThat(GraphExportStyles.getId("a(b)"), is("_a_b_"));
    }

    @Test
    public void testGetLabel() {
        List<Abbreviation> abbreviations = Lists.newArrayList(new Abbreviation("com.my", "C"), new Abbreviation("org.apache", "A"));

        assertThat(GraphExportStyles.getLabel(abbreviations, "com.my.any.Clazz"), is("C.any.Clazz"));
        assertThat(GraphExportStyles.getLabel(abbreviations, "org.apache.Helper"), is("A.Helper"));
        assertThat(GraphExportStyles.getLabel(abbreviations, "org.apache"), is("A"));
        assertThat(GraphExportStyles.getLabel(abbreviations, "org"), is("org"));
    }

    @Test
    public void testGetLabelNoAbbreviations() {
        assertThat(GraphExportStyles.getLabel(new ArrayList<>(), "com.my.any.Clazz"), is("com.my.any.Clazz"));
    }

    @Test(expected = NullPointerException.class)
    public void testGetLabelNullAbbreviations() {
        GraphExportStyles.getLabel(null, "com.my.any.Clazz");
    }

    @Test
    public void testGetLabelNullName() {
        assertThat(GraphExportStyles.getLabel(new ArrayList<>(), null), nullValue());
    }
}