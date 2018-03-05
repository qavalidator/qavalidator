package de.qaware.qav.input.javacode.impl;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link AsmUtil}
 */
public class AsmUtilTest {

    @Test
    public void testToClassName() {
        assertThat(AsmUtil.toClassName("java/util/List", true), is("java.util.List"));
        assertThat(AsmUtil.toClassName("de/qaware/qav/A$1", true), is("de.qaware.qav.A"));
        assertThat(AsmUtil.toClassName("de/qaware/qav/A$1", false), is("de.qaware.qav.A$1"));
        assertThat(AsmUtil.toClassName("Lde/qaware/qav/A;", true), is("de.qaware.qav.A"));
        assertThat(AsmUtil.toClassName("[Lde/qaware/qav/A;", true), is("de.qaware.qav.A"));
        assertThat(AsmUtil.toClassName("[[Lde/qaware/qav/A;", true), is("de.qaware.qav.A"));
        assertThat(AsmUtil.toClassName("byte[]", true), is("byte"));
        assertThat(AsmUtil.toClassName(null, true), nullValue());
    }
}