package de.qaware.qav.maven;

import de.qaware.qav.runner.QAvalidatorConfig;
import org.apache.maven.model.Build;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link QavMavenMojo}.
 *
 * @author QAware GmbH
 */
public class QavMavenMojoTest extends AbstractMojoTestCase {

    @Before
    protected void setUp() throws Exception {
        // required for mojo lookups to work
        super.setUp();
    }

    @Test
    public void testConfigure1() throws Exception {
        File testPom = new File(getBasedir(), "src/test/resources/test-pom-1.xml");

        QavMavenMojo qavMavenMojo = (QavMavenMojo) lookupMojo("qav", testPom);

        assertThat(qavMavenMojo, notNullValue());
        assertThat(qavMavenMojo.toString(), is("QavMavenMojo[project=<null>,useDefaultInputDirs=true,failOnError=false,analysisFilename=<null>,outputDir=<null>]"));
    }

    @Test
    public void testConfigure2() throws Exception {
        File testPom = new File(getBasedir(), "src/test/resources/test-pom-2.xml");

        QavMavenMojo qavMavenMojo = (QavMavenMojo) lookupMojo("qav", testPom);

        assertThat(qavMavenMojo, notNullValue());
        assertThat(qavMavenMojo.toString(), is("QavMavenMojo[project=<null>,useDefaultInputDirs=true,failOnError=false,analysisFilename=qa/my.qav,outputDir=<null>]"));
    }

    @Test
    public void testConfigure3() throws Exception {
        File testPom = new File(getBasedir(), "src/test/resources/test-pom-3.xml");

        QavMavenMojo qavMavenMojo = (QavMavenMojo) lookupMojo("qav", testPom);

        assertThat(qavMavenMojo, notNullValue());
        assertThat(qavMavenMojo.toString(), is("QavMavenMojo[project=<null>,useDefaultInputDirs=false,failOnError=true,analysisFilename=qa/my-own.qav,outputDir=target/qav-report]"));
    }

    @Test
    public void testInit() {
        QavMavenMojo qavMavenMojo = getQavMavenMojo();
        qavMavenMojo.setUseDefaultInputDirs(true);

        QAvalidatorConfig config = qavMavenMojo.createConfig();

        assertThat(config, notNullValue());

        assertThat(config.getInputDirs(), hasSize(1));
        assertThat(config.getInputDirs().get(0), is("target/classes"));
    }

    @Test
    public void testInitWithoutDefaultDirs() {
        QavMavenMojo qavMavenMojo = getQavMavenMojo();
        qavMavenMojo.setUseDefaultInputDirs(false);

        QAvalidatorConfig config = qavMavenMojo.createConfig();

        // without default dirs, the input dirs will not be set.
        assertThat(config.getInputDirs(), hasSize(0));
    }

    private QavMavenMojo getQavMavenMojo() {
        MavenProject mavenProject = getMavenProjectMock();

        QavMavenMojo qavMavenMojo = new QavMavenMojo();

        qavMavenMojo.setAnalysisFilename("qa/my.qav");
        qavMavenMojo.setFailOnError(false);
        qavMavenMojo.setOutputDir(null);
        qavMavenMojo.setProject(mavenProject);
        return qavMavenMojo;
    }

    private MavenProject getMavenProjectMock() {
        MavenProject mavenProject = mock(MavenProject.class);
        Build build = mock(Build.class);
        when(build.getDirectory()).thenReturn("target");
        when(build.getOutputDirectory()).thenReturn("target/classes");
        when(mavenProject.getBuild()).thenReturn(build);
        when(mavenProject.getBasedir()).thenReturn(new File("/home/qav"));
        when(mavenProject.getGroupId()).thenReturn("my.group");
        return mavenProject;
    }

    private String normalizePath(String path) {
        return path.replaceAll("\\\\", "/");
    }
}
