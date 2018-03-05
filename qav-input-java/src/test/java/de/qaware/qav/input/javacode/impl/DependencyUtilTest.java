package de.qaware.qav.input.javacode.impl;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link DependencyUtil}.
 *
 * @author QAware GmbH
 */
public class DependencyUtilTest {

    @Test
    public void testIsIgnorable() {
        assertThat(DependencyUtil.isIgnorable("java.lang.Object"), is(true));
        assertThat(DependencyUtil.isIgnorable("java.lang.String"), is(true));
        assertThat(DependencyUtil.isIgnorable("B"), is(true));
        assertThat(DependencyUtil.isIgnorable("int[][]"), is(true));

        assertThat(DependencyUtil.isIgnorable("java.util.List"), is(false));

        assertThat(DependencyUtil.isIgnorable(""), is(false));
    }

    @Test
    public void testIsIgnorableNoChecksForListEtc() {
        assertThat(DependencyUtil.isIgnorable("L[java.lang.Object"), is(false));
    }

    @Test
    public void testNullInput() {
        assertThat(DependencyUtil.isIgnorable(null), is(true));
    }
}