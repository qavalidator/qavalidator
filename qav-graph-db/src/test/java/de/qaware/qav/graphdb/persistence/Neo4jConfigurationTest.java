package de.qaware.qav.graphdb.persistence;

import org.junit.Test;
import org.neo4j.ogm.config.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link Neo4jConfiguration}
 */
public class Neo4jConfigurationTest {

    @Test
    public void testConfiguration() {
        Configuration configuration = Neo4jConfiguration.getConfiguration();

        assertThat(configuration).isNotNull();
        assertThat(configuration.getURI()).startsWith("bolt://");
    }

}