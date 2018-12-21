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
     * Adds the value to the list of values.
     * <p>
     * If the new value is a list, then add each entry (instead of adding the list as one nested entry). Only add new
     * entries if the value was not in the list before.
     * <p>
     * Creates the list if it does not exist so far. Changes the value to a {@link List} if it wasn't a List before -
     * but only if the value did not change.
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
        } else if (object.equals(value)) {
            return; // don't replace with a list if the value is the same, i.e. if there is no change.
        } else if (object instanceof List) {
            list = (List<Object>) object;
        } else {
            LOGGER.warn("Property {} was not a list, replacing with a list.", key);
            list = new ArrayList<>();
            list.add(object);
            properties.put(key, list);
        }

        // add new value. If it's a list, add each entry of that list:
        if (value instanceof List) {
            List<Object> newValues = (List<Object>) value;
            newValues.forEach(v -> {
                if (!list.contains(v)) {
                    list.add(v);
                }
            });
        } else if (!list.contains(value)) {
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
