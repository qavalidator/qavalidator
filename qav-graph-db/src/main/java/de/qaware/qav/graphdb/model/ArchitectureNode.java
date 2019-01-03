package de.qaware.qav.graphdb.model;

import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents an Architecture node, i.e. a component in the architecture tree.
 */
@NodeEntity
@Getter
@Setter
public class ArchitectureNode extends AbstractNode {

    public static final String CONTAINS_TYPE_NAME = "CONTAINS";
    public static final String IMPLEMENTED_BY_TYPE_NAME = "IMPLEMENTED_BY";

    private String architectureName;

    @Relationship(CONTAINS_TYPE_NAME)
    private Set<ArchitectureNode> children = new HashSet<>();

    @Relationship(value = CONTAINS_TYPE_NAME, direction = Relationship.INCOMING)
    private ArchitectureNode parent;

    @Relationship(IMPLEMENTED_BY_TYPE_NAME)
    private Set<ClassNode> implementations = new HashSet<>();

    /**
     * Constructor.
     *
     * @param name the name of the node
     */
    public ArchitectureNode(String name) {
        super(name);
    }
}
