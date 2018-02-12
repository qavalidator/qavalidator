package de.qaware.qav.sonar.parser;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link QavSonarResult}.
 *
 * @author QAware GmbH
 */
public class QavSonarResultTest {

    @Test
    public void testQavSonarResult() {
        QavSonarResult result = new QavSonarResult();

        assertThat(result.getNoWarnings(), is(0));
        assertThat(result.getNoErrors(), is(0));

        result.incErrors();
        result.incErrors();

        assertThat(result.getNoWarnings(), is(0));
        assertThat(result.getNoErrors(), is(2));

        result.incWarnings();

        assertThat(result.getNoWarnings(), is(1));
        assertThat(result.getNoErrors(), is(2));
    }

    @Test
    public void testAll() {
        QavSonarResult result = new QavSonarResult();
        result.setNoErrors(5);
        result.setNoWarnings(0);
        result.setEmpty(false);

        assertThat(result.toString(), is("QavSonarResult(isEmpty=false, noWarnings=0, noErrors=5)"));
    }

}