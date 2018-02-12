package de.qaware.qav.visualization;

import lombok.Getter;
import lombok.ToString;

/**
 * Line style for DOT or GraphML output.
 *
 * @author QAware GmbH
 */
@Getter
@ToString
public class LineStyle {

    private final String color;
    private final String width;
    private final String lineStyle;

    /**
     * the edge style for DOT export.
     * This is similar to {@link #lineStyle}, but it's e.g. "solid" instead of "line".
     */
    private final String dotLineStyle;

    /**
     * Constructor.
     *
     * @param color     color, must be a hex string with a leading '#'
     * @param width     line width, a string with a double number
     * @param lineStyle the style, may be line, dashed, dotted, dashed_dotted.
     *                  Will be adjusted for {@link #dotLineStyle}: <tt>"solid"</tt> instead of <tt>"line"</tt>
     */
    public LineStyle(String color, String width, String lineStyle) {
        this.color = color;
        this.width = width;
        this.lineStyle = lineStyle;
        this.dotLineStyle = "line".equals(lineStyle) ? "solid" : lineStyle;
    }

}
