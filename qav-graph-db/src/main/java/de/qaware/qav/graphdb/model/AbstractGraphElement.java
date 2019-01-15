package de.qaware.qav.graphdb.model;

import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.Properties;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class for graph elements.
 */
@Getter
@Setter
@SuppressWarnings("squid:S1694")
// Sonarqube wants this class not be abstract. However, there is no point in instantiating this class; instead, use one
// of the sub classes.
public abstract class AbstractGraphElement {

    @Id
    @GeneratedValue
    private Long id;

    @Properties(prefix = "custom")
    private Map<String, Object> properties = new HashMap<>();

    /**
     * Set the property. Overwrite an existing value.
     *
     * @param key   the key
     * @param value the value
     */
    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }

    /**
     * Check if the property exists.
     *
     * @param key name of the property
     * @return true if the property exists, false if not.
     */
    public boolean hasProperty(String key) {
        return properties.containsKey(key);
    }

    /**
     * Get the property with the given name.
     *
     * @param key name of the property
     * @return the value, or null if it does not exist.
     */
    public Object getProperty(String key) {
        return properties.get(key);
    }
}
