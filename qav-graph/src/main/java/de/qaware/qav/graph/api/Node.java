package de.qaware.qav.graph.api;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Node in the dependency graph.
 *
 * @author QAware GmbH
 */
public class Node extends AbstractGraphElement {

    /**
     * Constructor.
     *
     * A node always has a name which can not be changed.
     *
     * @param name the name of the node
     */
    public Node(String name) {
        checkNotNull(name, "Name may not be null");
        properties.put(NAME, name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Node other = (Node) o;
        return getName().equals(other.getName());
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public String toString() {
        return getName();
    }
}
