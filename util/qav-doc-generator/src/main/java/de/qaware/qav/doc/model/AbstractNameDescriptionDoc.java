package de.qaware.qav.doc.model;

import java.util.Objects;

/**
 * Base class for the description DTOs.
 *
 * @author QAware GmbH
 */
public abstract class AbstractNameDescriptionDoc {

    private String name;

    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AbstractNameDescriptionDoc that = (AbstractNameDescriptionDoc) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description);
    }

    @Override
    public String toString() {
        return "name='" + name + '\'' +
                ", description='" + description + '\'';
    }
}
