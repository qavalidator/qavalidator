package de.qaware.qav.test.beans;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Tests for generated methods.
 * <p>
 * @author QAware GmbH
 */
public class BeanTestUtil {
    /**
     * util class, no instances.
     */
    private BeanTestUtil() {
    }

    /**
     * o1 and o2 should be equal (but not same), while o3 is different.
     * <p>
     * This method calls the equals method of the given beans in each combination, so that all branches in the typical
     * generated equals method are covered.
     * <p>
     * Also calls toString() and hashCode() to touch all lines in generated methods.
     *
     * @param o1 o1 to compare
     * @param o2 o2 to compare
     * @param o3 o3 to compare
     */
    @SuppressWarnings({"ObjectEqualsNull", "EqualsWithItself"})
    public static <T> void checkEqualsMethod(T o1, T o2, T o3) {
        assertThat(o1.equals(null), is(false));
        assertThat(o1.equals(o1), is(true));
        assertThat(o1.equals("some-other-data-type"), is(false));
        assertThat(o1.equals(o2), is(true));
        assertThat(o1.equals(o3), is(false));

        assertThat(o1.toString(), notNullValue());
        assertThat(o1.hashCode(), is(o2.hashCode()));
        assertThat(o1.hashCode(), is(not(o3.hashCode())));
    }
}