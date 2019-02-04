package de.qaware.qav.visualization.impl;

import de.qaware.qav.graph.alg.api.CycleFinder;
import de.qaware.qav.graph.api.Dependency;
import de.qaware.qav.graph.api.Node;
import org.antlr.stringtemplate.StringTemplate;

/**
 * Util methods to style the DOT output.
 *
 * @author tilman
 */
public final class DotExportStyles {

    private static final String DEFAULT_STYLE = "color = black";
    private static final String CYCLE_STYLE = "color = red, penwidth = 3.0";
    private static final double VERTICAL_SPACE_FACTOR = 0.3;

    /**
     * Utils class with only static method.
     */
    private DotExportStyles() {
    }

    /**
     * provide the node style depending on whether it's part of a cycle or not
     *
     * @param node the {@link Node} to style
     * @return the node style in DOT syntax
     */
    public static String getNodeStyle(Node node) {
        return Boolean.TRUE.equals(node.getProperty(CycleFinder.IN_CYCLE)) ? CYCLE_STYLE : DEFAULT_STYLE;
    }

    /**
     * create the URL for a node.
     *
     * @param node the Node name
     * @return the URL for that node
     */
    public static String getNodeUrl(String node) {
        return "/#/node/" + node;
    }

    /**
     * provide the edge style depending on the type of the relationship.
     *
     * @param rel the {@link Dependency} to style
     * @return the edge style in DOT syntax
     */
    public static String getEdgeStyle(Dependency rel) {
        StringTemplate st = new StringTemplate("color = \"$color$\", fontcolor = \"$color$\", penwidth = $width$, style = $style$");
        EdgeStyle edgeStyle = GraphExportStyles.getEdgeStyle(rel);
        st.setAttribute("color", edgeStyle.getColor());
        st.setAttribute("width", edgeStyle.getWidth());
        st.setAttribute("style", edgeStyle.getDotLineStyle());

        return st.toString();
    }

    /**
     * calculate the ranksep, i.e. the vertical space between the nodes.
     *
     * @param noNodes number of nodes in the graph in total
     * @return the rankSep
     */
    public static double getRankSep(int noNodes) {
        return Math.sqrt(noNodes) * VERTICAL_SPACE_FACTOR;
    }


}
