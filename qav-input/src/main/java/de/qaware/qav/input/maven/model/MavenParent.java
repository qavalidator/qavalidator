package de.qaware.qav.input.maven.model;

import lombok.Data;

/**
 * Represents the parent block in a Maven pom.xml
 */
@Data
public class MavenParent {

    private String groupId;
    private String artifactId;
    private String version;
    private String relativePath;
}
