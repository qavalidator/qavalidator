package de.qaware.qav.graphdb.model;

import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

/**
 * Represents a class in the Cluster Graph, i.e. a node on the basic level.
 */
@NodeEntity
@Getter
@Setter
public class MethodNode extends AbstractNode {

    @Relationship(value = ClassNode.HAS_METHOD, direction = Relationship.INCOMING)
    private ClassNode implementedIn;

    /**
     * Constructor.
     *
     * @param name the name of the class
     */
    public MethodNode(String name) {
        super(name);
    }
}
