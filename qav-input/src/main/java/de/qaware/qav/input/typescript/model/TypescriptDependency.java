package de.qaware.qav.input.typescript.model;

import lombok.Data;

/**
 * XML model class to represent the input from the Typescript analysis tool.
 */
@Data
public class TypescriptDependency {

    /** The id of the source node. */
    private String from;

    /** The id of the target node. */
    private String to;

    /** The type of the dependency. */
    private String type;
}
