package de.qaware.qav.input.maven.model;

import lombok.Data;

/**
 *
 */
@Data
public class MavenParent {

    private String groupId;
    private String artifactId;
    private String version;
    private String relativePath;
}
