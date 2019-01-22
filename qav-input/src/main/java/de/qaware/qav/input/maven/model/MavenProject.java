package de.qaware.qav.input.maven.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Maven pom.xml file.
 */
@Data
@JacksonXmlRootElement(localName = "project")
@JsonIgnoreProperties(ignoreUnknown = true)
public class MavenProject {

    private String groupId;
    private String artifactId;

    private MavenParent parent;

    private List<String> modules = new ArrayList<>();

    private List<MavenDependency> dependencies = new ArrayList<>();
}
