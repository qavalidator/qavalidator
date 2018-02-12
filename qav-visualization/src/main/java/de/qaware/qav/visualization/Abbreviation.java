package de.qaware.qav.visualization;

import lombok.AllArgsConstructor;

/**
 * This is to beautify the output in the DOT graph.
 *
 * @author QAware GmbH
 */
@AllArgsConstructor
public class Abbreviation {

    /** the pattern to match */
    private final String pattern;
    /** the replacement to use */
    private final String replacement;

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
