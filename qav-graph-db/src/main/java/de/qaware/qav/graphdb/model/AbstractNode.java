package de.qaware.qav.graphdb.model;

import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a node in the graph; either on basic level (i.e. classes), or on the structural level (i.e. architecture
 * tree).
 */
public abstract class AbstractNode extends AbstractGraphElement {

    @Getter
    private String name;

    @Relationship(type = ReferencesRelation.REFERENCES_TYPE_NAME)
    @Getter
    @Setter
    private Set<ReferencesRelation> referencesRelations = new HashSet<>();

    /**
     * Constructor.
     *
     * @param name name of the node
     */
    public AbstractNode(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
