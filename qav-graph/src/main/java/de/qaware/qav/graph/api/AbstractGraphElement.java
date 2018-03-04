package de.qaware.qav.graph.api;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract base class for Nodes and Dependencies.
 * <p>
 * Is able to carry properties.
 *
 * @author QAware GmbH
 */
@SuppressWarnings("squid:S1694")
// warns that there is no abstract method, i.e. no abstract behaviour that is encapsulated here. However, this class
// contains the commonalities of elements in a graph and is therefore fine.
public abstract class AbstractGraphElement {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractGraphElement.class);

    /**
     * name of the property.
     */
    public static final String NAME = "name";

    /**
     * properties of the node or edge.
     */
    protected final Map<String, Object> properties = new HashMap<>();

    /**
     * get the name.
     *
     * @return the name.
     */
    public String getName() {
        return (String) properties.get(NAME);
    }

    /**
     * gets the property, or null if it does not exist.
     *
     * @param key name of the property.
     * @return the value of the property, or null if it does not exist.
     */
    public Object getProperty(String key) {
        return properties.get(key);
    }

    /**
     * gets the property, or the default value if it does not exist.
     *
     * @param key          name of the property
     * @param defaultValue the default value to return if no value exists
     * @param <T>          the type of the default value
     * @return the property, or the default value if there is no such property.
     */
    @SuppressWarnings("unchecked")
    public <T> T getProperty(String key, T defaultValue) {
        return (T) properties.getOrDefault(key, defaultValue);
    }

    /**
     * Checks whether this element has a property with the given key
     *
     * @param key name of the property.
     * @return true if the property exists, false if it doesn't.
     */
    public boolean hasProperty(String key) {
        return properties.containsKey(key);
    }

    /**
     * Sets the property. Replaces the existing value, or removes the property if the value is null.
     *
     * @param key   name of the property.
     * @param value the value of the property. If null, it will remove the property.
     */
    public void setProperty(String key, Object value) {
        Preconditions.checkNotNull(key);

        if (key.equals(NAME)) {
            throw new IllegalArgumentException("It's not allowed to change the name.");
        }

        if (value == null) {
            properties.remove(key);
        } else {
            properties.put(key, value);
        }
    }

    /**
     * Adds the value to the list of values. Only adds if the value was not in the list before. Creates the list if it
     * does not exist so far. Changes the value to a {@link List} if it wasn't a List before.
     *
     * @param key   name of the property
     * @param value value to add.
     */
    @SuppressWarnings("unchecked")
    public void addListProperty(String key, Object value) {
        List<Object> list;

        if (value == null) {
            return;
        }

        Object object = properties.get(key);
        if (object == null) {
            list = new ArrayList<>();
            properties.put(key, list);
        } else if (object instanceof List) {
            list = (List<Object>) object;
        } else {
            LOGGER.warn("Property {} was not a list, replacing with a list.", key);
            list = new ArrayList<>();
            list.add(object);
            properties.put(key, list);
        }
        if (!list.contains(value)) {
            list.add(value);
        }
    }

    /**
     * returns a copy of the properties map.
     *
     * @return a copy of the properties map.
     */
    public Map<String, Object> getProperties() {
        return new HashMap<>(properties);
    }
}
