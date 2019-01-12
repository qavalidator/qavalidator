package de.qaware.qav.input.typescript.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * XML model class to represent the input from the Typescript analysis tool.
 */
@Data
public class TypescriptEntity {

    /** The name of the entity. */
    private String name;

    /** The type of the entity. */
    private String type;

    /** The id; used to set up the dependencies. */
    private String id;

    /** The child entities. */
    @JacksonXmlElementWrapper(localName = "entity", useWrapping = false)
    @JsonProperty("entity")
    private List<TypescriptEntity> entities = new ArrayList<>();
}
