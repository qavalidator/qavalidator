package de.qaware.qav.visualization.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

/**
 * This is to beautify the output in the DOT graph.
 *
 * @author QAware GmbH
 */
@AllArgsConstructor
@EqualsAndHashCode
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
