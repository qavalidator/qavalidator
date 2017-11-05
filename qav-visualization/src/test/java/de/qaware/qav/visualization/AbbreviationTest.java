package de.qaware.qav.visualization;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * @author QAware GmbH
 */
public class AbbreviationTest {

    @Test
    public void testAbbreviate() throws Exception {
        Abbreviation abbreviation = new Abbreviation("de.qaware", "Q");

        assertThat(abbreviation.abbreviate("com.something"), is("com.something"));
        assertThat(abbreviation.abbreviate("de.something"), is("de.something"));
        assertThat(abbreviation.abbreviate("de.qaware"), is("Q"));
        assertThat(abbreviation.abbreviate("de.qaware.qav"), is("Q.qav"));

        assertThat(abbreviation.abbreviate(""), is(""));
        assertThat(abbreviation.abbreviate(null), nullValue());
    }

    @Test
    public void testAbbreviateWithRegExp() throws Exception {
        Abbreviation abbreviation = new Abbreviation(".*\\.qaware", "Q");

        assertThat(abbreviation.abbreviate("de.qaware"), is("Q"));
        assertThat(abbreviation.abbreviate("com.qaware"), is("Q"));
        assertThat(abbreviation.abbreviate("de.qaware.qav"), is("Q.qav"));
        assertThat(abbreviation.abbreviate("com.qaware.qav"), is("Q.qav"));
    }
}