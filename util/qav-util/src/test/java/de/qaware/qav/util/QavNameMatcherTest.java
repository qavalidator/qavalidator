package de.qaware.qav.util;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link QavNameMatcher}.
 *
 * @author QAware GmbH
 */
public class QavNameMatcherTest {

    private final QavNameMatcher qavNameMatcher = new QavNameMatcher();
    private final QavNameMatcher hashMatcher = new QavNameMatcher("#");

    @Test
    public void testShortNames() {
        assertThat(qavNameMatcher.matches("xy.**", "xy"), is(true));
        assertThat(qavNameMatcher.matches("xy.*", "xy"), is(true));
        assertThat(qavNameMatcher.matches("xy", "xy"), is(true));

        assertThat(qavNameMatcher.matches("v*", "v1"), is(true));
        assertThat(qavNameMatcher.matches("v*", "x1"), is(false));
    }

    @Test
    public void testOneFinalPackage() {
        assertThat(qavNameMatcher.matches("com.my.project.module.*", "com.my.project.module.a.A1"), is(true));
        assertThat(qavNameMatcher.matches("com.my.project.module.*", "com.my.project.module.a.b.c.A1"), is(true));
        assertThat(qavNameMatcher.matches("com.my.project.module.*", "com.my.project.module.A1"), is(true));
        assertThat(qavNameMatcher.matches("com.my.project.module.*", "com.my.project.util.A1"), is(false));

        assertThat(qavNameMatcher.matches("com.my.project.module.*", "com.my.project.module"), is(true));
        assertThat(qavNameMatcher.matches("com.my.project.module.*", "com.my.project.module."), is(true));
    }

    @Test
    public void testMultipleWildcards() {
        assertThat(qavNameMatcher.matches("com.my.*.module.*", "com.my.project.module.a.A1"), is(true));
        assertThat(qavNameMatcher.matches("com.my.*.module.a.*", "com.my.project.module.a.A1"), is(true));
        assertThat(qavNameMatcher.matches("com.my.*.module.**", "com.my.project.module.a.A1"), is(true));
        assertThat(qavNameMatcher.matches("com.my.*.module.*", "com.my.p2.subprj.module.a.A1"), is(false));
        assertThat(qavNameMatcher.matches("com.my.**.module.*", "com.my.p2.subprj.module.a.A1"), is(true));
    }

    @Test
    public void testAnyPackages() {
        assertThat(qavNameMatcher.matches("com.my.**.module.Clazz", "com.my.p2.subprj.module.Clazz"), is(true));
        assertThat(qavNameMatcher.matches("com.my.**.module.Clazz", "com.my.p2.subprj.module.impl.Clazz"), is(false));

        assertThat(qavNameMatcher.matches("**3**", "3rdParty"), is(true));
        assertThat(qavNameMatcher.matches("**.*3**", "org.apache.commons.lang3"), is(true));
        assertThat(qavNameMatcher.matches("**.*3.**", "org.apache.commons.lang3.ArrayUtils"), is(true));

        assertThat(qavNameMatcher.matches("*", "org.apache.commons.lang3.ArrayUtils"), is(true));
    }

    @Test
    public void testPackageSeparators() {
        assertThat(qavNameMatcher.matches("**.commons**", "org.apache.commons.lang3"), is(false));
        assertThat(qavNameMatcher.matches("**.commons.**", "org.apache.commons.lang3"), is(true)); // the package '.' makes a difference
    }

    @Test
    public void testOtherCharacters() {
        assertThat(qavNameMatcher.matches("com.my.module$submodule.*", "com.my.module$submodule.Clazz"), is(true));
        assertThat(qavNameMatcher.matches("com.my.module.*", "com.my.module.Clazz$Subclazz"), is(true));
    }

    /**
     * test behavior with '.*' at the end
     */
    @Test
    public void testFinalPackage() {
        assertThat(qavNameMatcher.matches("org.slf4j.*", "org.slf4j.Logger"), is(true));
        assertThat(qavNameMatcher.matches("org.slf4j.*", "org.slf4j"), is(true));
        assertThat(qavNameMatcher.matches("org.slf4j**", "org.slf4j"), is(true)); // without package dot
        assertThat(qavNameMatcher.matches("org.slf4j**", "org.slf4jOrSo"), is(true)); // without package dot
        assertThat(qavNameMatcher.matches("org.slf4j**", "org.slf4j.Logger"), is(false)); // package dot is missing
    }

    @Test(expected = NullPointerException.class)
    public void testNullInputPattern() {
        qavNameMatcher.matches(null, "any");
    }

    @Test(expected = NullPointerException.class)
    public void testNullInputName() {
        qavNameMatcher.matches("pattern", null);
    }


    @Test
    public void testOtherSeparators() {
        assertThat(hashMatcher.matches("my/dir/file#class#*", "my/dir/file#class#method"), is(true));
        assertThat(hashMatcher.matches("my/dir/file#*", "my/dir/file#class#method"), is(true));
        assertThat(hashMatcher.matches("my/dir/file#**", "my/dir/file#class#method"), is(true));

        assertThat(hashMatcher.matches("my/dir/file*", "my/dir/file#class#method"), is(false));
        assertThat(hashMatcher.matches("my/dir/file**", "my/dir/file#class#method"), is(false));
        assertThat(hashMatcher.matches("my/dir/file.**", "my/dir/file#class#method"), is(false));
    }
}