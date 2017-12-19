package de.qaware.qav.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.qaware.qav.util.test.A;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Tests for {@link FileNameUtil}.
 *
 * @author QAware GmbH
 */
public class FileNameUtilTest {

    private static final String TEST_CLASSES_DIR = "build/classes/java/test/";

    @Test
    public void testIdentifyClassFilesWithAntScanner() {
        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("baseDir", TEST_CLASSES_DIR);
        parameters.put("includes", "**/*.class");

        List<File> files = FileNameUtil.identifyFiles(parameters);

        String thisFileName = this.getClass().getCanonicalName().replaceAll("\\.", "/");
        File thisTestFile = new File(TEST_CLASSES_DIR + thisFileName + ".class");
        assertThat(files.contains(new File(thisTestFile.getPath())), CoreMatchers.is(true));

        String aTestFileName = ProcessUtilTest.class.getCanonicalName().replaceAll("\\.", "/");
        File aTestFile = new File(TEST_CLASSES_DIR + aTestFileName + ".class");
        assertThat(files.contains(new File(aTestFile.getPath())), CoreMatchers.is(true));
    }

    @Test
    public void testIdentifyClassFilesWithAntScannerWithExcludes() {
        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("baseDir", TEST_CLASSES_DIR);
        parameters.put("includes", "**/*.class");
        parameters.put("excludes", "**/*Test.class");

        List<File> files = FileNameUtil.identifyFiles(parameters);

        String thisFileName = this.getClass().getCanonicalName().replaceAll("\\.", "/");
        File thisTestFile = new File(TEST_CLASSES_DIR + thisFileName + ".class");
        assertThat(files.contains(new File(thisTestFile.getPath())), CoreMatchers.is(false));

        String aTestFileName = A.class.getCanonicalName().replaceAll("\\.", "/");
        File aTestFile = new File(TEST_CLASSES_DIR + aTestFileName + ".class");
        assertThat(files.contains(new File(aTestFile.getPath())), CoreMatchers.is(true));
    }

    @Test
    public void testIdentifyClassFilesWithAntScannerWithListIncludesExcludes() {
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("baseDir", TEST_CLASSES_DIR);
        parameters.put("includes", Arrays.asList("**/*ReaderTest.class", "**/*UtilTest.class"));
        parameters.put("excludes", Collections.singletonList("**/Java*.class"));

        List<File> files = FileNameUtil.identifyFiles(parameters);

        String aTestFileName = JarFileUtilTest.class.getCanonicalName().replaceAll("\\.", "/");
        File aTestFile = new File(TEST_CLASSES_DIR + aTestFileName + ".class");
        assertThat(files.contains(new File(aTestFile.getPath())), CoreMatchers.is(true));

        String bTestFileName = ClassHandler.class.getCanonicalName().replaceAll("\\.", "/");
        File bTestFile = new File(TEST_CLASSES_DIR + bTestFileName + ".class");
        assertThat(files.contains(new File(bTestFile.getPath())), CoreMatchers.is(false));
    }

    @Test
    public void testIdentifyClassFilesWithNotExistingDir() {
        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("baseDir", "not/existing/dir");
        parameters.put("includes", "**/*.class");
        parameters.put("excludes", "**/*Test.class");

        List<File> files = FileNameUtil.identifyFiles(parameters);

        assertThat(files, hasSize(0));
    }

    @Test
    public void testIdentifyClassFilesWithoutBaseDir() {
        Map<String, String> parameters = Maps.newHashMap();
        // no baseDir given
        parameters.put("includes", "**/*.class");
        parameters.put("excludes", "**/*Test.class");

        List<File> files = FileNameUtil.identifyFiles(parameters);

        assertThat(files, hasSize(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIdentifyClassFilesWithWrongInput() {
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("baseDir", TEST_CLASSES_DIR);
        Set<String> input = new HashSet<>();
        input.add("**/*.class");
        parameters.put("includes", input);

        FileNameUtil.identifyFiles(parameters);
    }

    @Test
    public void testMatches() {
        assertThat(FileNameUtil.matches("a/b/c/d/e.txt", "a/**/e.txt"), is(true));
        assertThat(FileNameUtil.matches("a/b/c/d/e.txt", "a/*/e.txt"), is(false));
        assertThat(FileNameUtil.matches("a/b/c/d/e.txt", "**/e.txt"), is(true));
        assertThat(FileNameUtil.matches("a/b/c/d/e.txt", "*/e.txt"), is(false));

        assertThat(FileNameUtil.matches("a/b/c/d/e.txt", new String[] {"**/e.txt"}, false), is(true));
        assertThat(FileNameUtil.matches("a/b/c/d/e.txt", new String[] {"*/e.txt"}, true), is(false));

        assertThat(FileNameUtil.matches("a/b/c/d/e.txt", null, false), is(false));
        assertThat(FileNameUtil.matches("a/b/c/d/e.txt", null, true), is(true));

    }

    @Test
    public void isIncluded() {
        Map<String, Object> parameters = ImmutableMap.<String, Object>builder().put("includes", "**/qav-core*.jar").build();
        assertIncluded("BOOT-INF/lib/qav-core.jar", parameters, true);

        parameters = ImmutableMap.<String, Object>builder().put("includes", "*/qav-core*.jar").build();
        assertIncluded("BOOT-INF/lib/qav-core.jar", parameters, false);

        parameters = ImmutableMap.<String, Object>builder().put("includes", "*/qav-core*.jar").build();
        assertIncluded("BOOT-INF/lib/qav-core.jar", parameters, false);

        parameters = ImmutableMap.<String, Object>builder().put("excludes", "**/qav-core*.jar").build();
        assertIncluded("BOOT-INF/lib/qav-core.jar", parameters, false);

        parameters = ImmutableMap.<String, Object>builder()
                .put("includes", "**/*.jar")
                .put("excludes", "**/qav*").build();
        assertIncluded("BOOT-INF/lib/qav-core.jar", parameters, false);
        assertIncluded("BOOT-INF/lib/other.jar", parameters, true);

        parameters = ImmutableMap.<String, Object>builder()
                .put("includes", new ArrayList<>())
                .put("excludes", new ArrayList<>()).build();
        assertIncluded("BOOT-INF/lib/qav-core.jar", parameters, true);

        assertIncluded("BOOT-INF/lib/qav-core.jar", null, true);
    }

    private void assertIncluded(String name, Map parameters, boolean expected) {
        assertThat(FileNameUtil.isIncluded(name, parameters), is(expected));
    }


    @Test
    public void testGetAsArray() {
        assertThat(FileNameUtil.getAsArray(null), nullValue());
        assertThat(FileNameUtil.getAsArray(""), CoreMatchers.is(new String[] {""}));
        assertThat(FileNameUtil.getAsArray(Lists.newArrayList("a", "b", "c")), CoreMatchers.is(new String[] {"a", "b", "c"}));

        try {
            assertThat(FileNameUtil.getAsArray(12L), nullValue());
            fail("IllegalArgumentException expected");
        } catch(IllegalArgumentException e) {
            assertThat(e.getMessage(), CoreMatchers.is("includes or excludes must be a String or a List<String> but is a class java.lang.Long"));
        }
    }

    @Test
    public void testGetBasePath() {
        assertThat(FileNameUtil.getParentPath("classpath:/visualization/DOT.stg"), Matchers.is("classpath:/visualization/"));
        assertThat(FileNameUtil.getParentPath("classpath:/application.properties"), Matchers.is("classpath:/"));
        assertThat(FileNameUtil.getParentPath("/home/user/project/x.txt"), endsWith("home" + File.separator + "user" + File.separator + "project" + File.separator));
        assertThat(FileNameUtil.getParentPath("build.gradle"), endsWith(File.separator));
    }


    @Test
    public void testGetCanonicalPath() {
        String result = FileNameUtil.getCanonicalPath("/x/y/z");
        result = result.replaceAll("\\\\", "/"); // normalize, for tests on Unix.
        assertThat(result.endsWith("/x/y/z"), CoreMatchers.is(true));

        result = FileNameUtil.getCanonicalPath("build/results/./xyz");
        result = result.replaceAll("\\\\", "/"); // normalize, for tests on Unix.
        assertThat(result.endsWith("build/results/xyz"), CoreMatchers.is(true));

        result = FileNameUtil.getCanonicalPath("classpath://qa/analysis.groovy");
        assertThat(result, CoreMatchers.is("classpath://qa/analysis.groovy"));
    }
}