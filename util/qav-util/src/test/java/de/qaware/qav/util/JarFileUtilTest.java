package de.qaware.qav.util;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link JarFileUtil}.
 *
 * @author QAware GmbH
 */
public class JarFileUtilTest {

    @Test
    public void testFindClassFiles() {
        File jarFile = getJarFile("src/test/resources/jars/qav-doc-generator.jar");
        Map<String, String> parameters = new HashMap<>();
        parameters.put("includes", "**/*.class");

        final Map<String, Integer> entries = new HashMap<>();
        ClassHandler classHandler = (name, content) -> entries.put(name, content.length);
        JarFileUtil.readJarFile(jarFile, parameters, classHandler);
        assertThat(entries.size(), is(14));
    }

    @Test
    public void testFindNestedJarFiles() {
        File jarFile = getJarFile("src/test/resources/jars/nesting.jar");
        Map<Object, Object> parameters = new HashMap<>();
        parameters.put("includes", Lists.newArrayList("**/qav-doc-gen*.jar", "**/*.txt"));

        final Map<String, Integer> entries = new HashMap<>();
        ClassHandler classHandler = (name, content) -> entries.put(name, content.length);
        JarFileUtil.readJarFile(jarFile, parameters, classHandler);
        assertThat(entries.size(), is(1));
        assertThat(entries.get("WEB-INF/classes/my-resource.txt"), is(24));
    }

    /**
     * checks that the JAR file exists.
     *
     * @param name name of the JAR file.
     * @return the {@link JarFile}
     */
    private File getJarFile(String name) {
        File file = new File(name);
        assertThat(file.getAbsolutePath() + " not found.", file.exists(), is(true));
        return file;
    }
}