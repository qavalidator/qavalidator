package de.qaware.qav.graph.filter;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Tests for small filters.
 *
 * @author QAware GmbH
 */
public class FilterTest {

    @Test
    public void testTrueAndNotFilter() throws Exception {
        TrueFilter trueFilter = new TrueFilter();
        assertThat(trueFilter.isAccepted(null), is(true));

        NotFilter notFilter = new NotFilter(trueFilter);
        assertThat(notFilter.isAccepted(null), is(false));

        NotFilter doubleNotFilter = new NotFilter(notFilter);
        assertThat(doubleNotFilter.isAccepted(null), is(true));
    }
}