package de.qaware.qav.graphdb.model;

import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a class in the Cluster Graph, i.e. a node on the basic level.
 */
@NodeEntity
@Getter
@Setter
public class ClassNode extends AbstractNode {

    public static final String HAS_METHOD = "HAS_METHOD";

    @Relationship(value = ArchitectureNode.IMPLEMENTED_BY_TYPE_NAME, direction = Relationship.INCOMING)
    private Set<ArchitectureNode> implementationFor = new HashSet<>();

    @Relationship(value = HAS_METHOD)
    private Set<MethodNode> methods = new HashSet<>();

    /**
     * Constructor.
     *
     * @param name the name of the class
     */
    public ClassNode(String name) {
        super(name);
    }
}
