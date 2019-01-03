package de.qaware.qav.graphdb.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a "Reference" relation, where one class or architecture component references another.
 */
@RelationshipEntity(type = ReferencesRelation.REFERENCES_TYPE_NAME)
@Getter
@Setter
@NoArgsConstructor
public class ReferencesRelation extends AbstractGraphElement {

    /** Type name, used as label in Neo4j. */
    public static final String REFERENCES_TYPE_NAME = "REFERENCES";

    /** The reference type. */
    private String referenceType;

    /**
     * The line numbers where the references are in the source code. Empty for {@link ReferencesRelation}s on
     * architecture level.
     */
    private List<Long> lineNo = new ArrayList<>();

    @StartNode
    private AbstractNode from;

    @EndNode
    private AbstractNode to;

    /**
     * Constructor.
     *
     * @param from          start node
     * @param to            end node
     * @param referenceType reference type
     */
    public ReferencesRelation(AbstractNode from, AbstractNode to, String referenceType) {
        this.from = from;
        this.to = to;
        this.referenceType = referenceType;
    }

    @Override
    public String toString() {
        return "(" + getNodeName(from) + ")-[" + this.referenceType + "]->(" + getNodeName(to) + ")";
    }

    private String getNodeName(AbstractNode node) {
        return node != null ? node.getName() : "<NONE>";
    }

}
