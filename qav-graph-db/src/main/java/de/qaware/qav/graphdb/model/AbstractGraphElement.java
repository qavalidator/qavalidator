package de.qaware.qav.graphdb.model;

import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.Properties;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@Getter
@Setter
public abstract class AbstractGraphElement {

    @Id
    @GeneratedValue
    private Long id;

    @Properties(prefix = "custom")
    private Map<String, Object> properties = new HashMap<>();


    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }

    public boolean hasProperty(String key) {
        return properties.containsKey(key);
    }

    public Object getProperty(String key) {
        return properties.get(key);
    }
}
