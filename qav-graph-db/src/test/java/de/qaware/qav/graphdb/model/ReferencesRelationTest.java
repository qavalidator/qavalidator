package de.qaware.qav.graphdb.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ReferencesRelation}.
 */
public class ReferencesRelationTest {

    @Test
    public void testReferenceRelation() {
        ClassNode c1 = new ClassNode("c1");
        ClassNode c2 = new ClassNode("c2");

        ReferencesRelation r1 = new ReferencesRelation(c1, c2, "READ_WRITE");
        assertThat(r1.toString()).isEqualTo("(c1)-[READ_WRITE]->(c2)");

        ReferencesRelation r2 = new ReferencesRelation();
        assertThat(r2.toString()).isEqualTo("(<NONE>)-[null]->(<NONE>)");
    }
}