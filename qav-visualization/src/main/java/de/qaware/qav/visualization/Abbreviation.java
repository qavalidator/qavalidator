package de.qaware.qav.visualization;

/**
 * This is to beautify the output in the DOT graph.
 *
 * @author QAware GmbH
 */
public class Abbreviation {

    private final String pattern;
    private final String replacement;

    /**
     * Constructor.
     *
     * @param pattern     the pattern to match
     * @param replacement the replacement to use
     */
    public Abbreviation(String pattern, String replacement) {
        this.pattern = pattern;
        this.replacement = replacement;
    }

    /**
     * applies the abbreviation pattern and replacement.
     *
     * @param s the String to abbreviate
     * @return the abbreviation
     */
    public String abbreviate(String s) {
        if (s == null) {
            return null;
        }
        return s.replaceAll(pattern, replacement);
    }
}
