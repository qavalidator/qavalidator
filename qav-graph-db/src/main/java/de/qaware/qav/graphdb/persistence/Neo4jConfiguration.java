package de.qaware.qav.graphdb.persistence;

import lombok.extern.slf4j.Slf4j;
import org.neo4j.ogm.config.Configuration;

/**
 * Create the Neo4j configuration.
 *
 * Reads the values from environment variables. If they are not set, falls back to default values.
 */
@Slf4j
public final class Neo4jConfiguration {

    public static final String ENV_URI = "NEO4J_URI";
    public static final String ENV_USERNAME = "NEO4J_USERNAME";
    public static final String ENV_SECRET = "NEO4J_PASSWORD";

    /** util class, no instances. */
    private Neo4jConfiguration() {
    }

    /**
     * Create the {@link Configuration}.
     *
     * Reads the values from environment variables. If they are not set, falls back to default values.
     *
     * @return the {@link Configuration}
     */
    public static Configuration getConfiguration() {
        String uri = getEnvOrDefault(ENV_URI, "bolt://localhost");
        String username = getEnvOrDefault(ENV_USERNAME, "neo4j");
        String password = getEnvOrDefault(ENV_SECRET, "neo4j");

        LOGGER.info("Creating configuration for: {} with username: {}", uri, username);

        return new Configuration.Builder()
                .uri(uri)
                .credentials(username, password)
                .build();
    }

    private static String getEnvOrDefault(String key, String fallback) {
        String value = System.getenv(key);

        if (value == null) {
            LOGGER.info("Environment variable {} not defined, falling back to default", key, fallback);
            return fallback;
        }

        return value;
    }
}
