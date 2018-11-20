package de.qaware.qav.visualization;

import com.google.common.collect.Maps;
import de.qaware.qav.graph.api.Dependency;
import de.qaware.qav.graph.api.DependencyType;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Style utils which are common to DOT export and GraphML export.
 *
 * @author QAware GmbH
 */
public final class GraphExportStyles {

    // Colors
    private static final String BLACK = "#000000";

    /**
     * These colors are taken from: http://www.graphviz.org/doc/info/colors.html#brewer, from the "rdbu11" color scheme
     */
    private static final String COLOR_1 = "#67001f";
    private static final String COLOR_2 = "#b2182b";
    private static final String COLOR_3 = "#d6604d";
    private static final String COLOR_9 = "#4393c3";
    private static final String COLOR_10 = "#2166ac";
    private static final String COLOR_11 = "#053061";

    // Line thickness
    private static final String THIN = "1.0";
    private static final String MEDIUM = "2.0";
    private static final String THICK = "3.0";

    // Line style
    private static final String DASHED = "dashed";
    private static final String STANDARD_LINE = "line";

    // Default line style
    private static final EdgeStyle DEFAULT_LINE_STYLE = new EdgeStyle(BLACK, THIN, STANDARD_LINE);

    private static final Map<DependencyType, EdgeStyle> EDGE_STYLES = Maps.newHashMap();

    static {
        // @formatter:off
        EDGE_STYLES.put(DependencyType.REFERENCE,    new EdgeStyle(COLOR_11, THIN,  DASHED));
        EDGE_STYLES.put(DependencyType.READ_ONLY,    new EdgeStyle(COLOR_10, THIN,  STANDARD_LINE));
        EDGE_STYLES.put(DependencyType.ANNOTATED_BY, new EdgeStyle(COLOR_9,  THIN,  DASHED));
        EDGE_STYLES.put(DependencyType.INJECTED,     new EdgeStyle(COLOR_3,  THIN,  STANDARD_LINE));
        EDGE_STYLES.put(DependencyType.READ_WRITE,   DEFAULT_LINE_STYLE);
        EDGE_STYLES.put(DependencyType.CREATE,       new EdgeStyle(COLOR_2,  THICK,  STANDARD_LINE));
        EDGE_STYLES.put(DependencyType.INHERIT,      new EdgeStyle(COLOR_1,  THICK,  DASHED));

        EDGE_STYLES.put(DependencyType.TEST,         new EdgeStyle(COLOR_11, THIN,   STANDARD_LINE));
        EDGE_STYLES.put(DependencyType.COMPILE,      DEFAULT_LINE_STYLE);
        EDGE_STYLES.put(DependencyType.PROVIDED,     new EdgeStyle(COLOR_3,  THIN,   DASHED));
        EDGE_STYLES.put(DependencyType.RUNTIME,      new EdgeStyle(COLOR_2,  MEDIUM, DASHED));
        EDGE_STYLES.put(DependencyType.CONTAINS,     new EdgeStyle(COLOR_1,  THIN,   STANDARD_LINE));
        // @formatter:on
    }

    /**
     * util class, without instances
     */
    private GraphExportStyles() {
    }

    /**
     * provide the edge style depending on the type of the relationship.
     *
     * @param rel the {@link Dependency} to style
     * @return the edge {@link EdgeStyle}
     */
    public static EdgeStyle getEdgeStyle(Dependency rel) {
        return EDGE_STYLES.getOrDefault(rel.getDependencyType(), DEFAULT_LINE_STYLE);
    }

    /**
     * turn the class name into a String that DOT and GraphML recognize as a valid ID.
     *
     * @param classname the class name
     * @return the id, to be used in a DOT or GraphML file
     */
    @SuppressWarnings("squid:S4248") // wants to move the RegExes into constants; would not help readability here.
    public static String getId(String classname) {
        return "_" + classname.replaceAll("<", "_")
                .replaceAll(">", "_")
                .replaceAll("\\s+", "_")
                .replaceAll("\\.", "_")
                .replaceAll("\\$", "_")
                .replaceAll(":", "_")
                .replaceAll("/", "_")
                .replaceAll("-", "_");
    }

    /**
     * apply the abbreviations to shorten the name.
     *
     * @param abbreviations the List of {@link Abbreviation}s to apply. May not be null.
     * @param name          the name to shorten
     * @return the shortened name
     */
    @SuppressWarnings("squid:S4248") // wants to move the RegExes into constants; would not help readability here.
    public static String getLabel(List<Abbreviation> abbreviations, String name) {
        checkNotNull(abbreviations, "abbreviations");

        if (name == null) {
            return null;
        }

        String result = name;
        for (Abbreviation abbreviation : abbreviations) {
            result = abbreviation.abbreviate(result);
        }

        result = result.replaceAll("_ROOT$", "");
        return result;
    }

}
