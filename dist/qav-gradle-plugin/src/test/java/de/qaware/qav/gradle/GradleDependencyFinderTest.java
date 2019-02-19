package de.qaware.qav.gradle;

import de.qaware.qav.graph.api.DependencyGraph;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link GradleDependencyFinder}
 */
public class GradleDependencyFinderTest {

    @Test
    public void testDependencyFinder() {
        Project project = createMockProject();

        assertThat(project).isNotNull();

        GradleDependencyFinder depFinder = new GradleDependencyFinder();
        depFinder.findDependencies(project);

        DependencyGraph result = depFinder.getDependencyGraph();
        assertThat(result).isNotNull();
        assertThat(result.getAllNodes()).hasSize(3);
    }

    private Project createMockProject() {
        Project parent = Mockito.mock(Project.class);
        when(parent.getGroup()).thenReturn("mytestgroup");
        when(parent.getName()).thenReturn("mytestprj");
        when(parent.getVersion()).thenReturn("1.0.0-SNAPSHOT");

        Project result = Mockito.mock(Project.class);
        when(result.getParent()).thenReturn(parent);

        when(result.getGroup()).thenReturn("mytestgroup");
        when(result.getName()).thenReturn("mytestprj");
        when(result.getVersion()).thenReturn("1.0.0-SNAPSHOT");

        Project child1 = Mockito.mock(Project.class);
        when(child1.getParent()).thenReturn(parent);

        when(child1.getGroup()).thenReturn("mytestgroup");
        when(child1.getName()).thenReturn("c1");
        when(child1.getVersion()).thenReturn("1.0.0-SNAPSHOT");
        when(child1.getChildProjects()).thenReturn(new HashMap<>());
        when(child1.getConfigurations()).thenReturn(Mockito.mock(ConfigurationContainer.class));

        Project child2 = Mockito.mock(Project.class);
        when(child2.getParent()).thenReturn(parent);

        when(child2.getGroup()).thenReturn("mytestgroup");
        when(child2.getName()).thenReturn("c2");
        when(child2.getVersion()).thenReturn("1.0.0-SNAPSHOT");
        when(child2.getChildProjects()).thenReturn(new HashMap<>());
        when(child2.getConfigurations()).thenReturn(Mockito.mock(ConfigurationContainer.class));


        HashMap<String, Project> children = new HashMap<>();
        children.put("c1", child1);
        children.put("c2", child2);
        when(result.getChildProjects()).thenReturn(children);
        ConfigurationContainer configurationContainer = Mockito.mock(ConfigurationContainer.class);
        when(result.getConfigurations()).thenReturn(configurationContainer);
        return result;
    }


}