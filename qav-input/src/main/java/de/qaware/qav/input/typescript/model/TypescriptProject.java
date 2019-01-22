package de.qaware.qav.input.typescript.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * XML model class to represent the input from the Typescript analysis tool.
 */
@Data
@JacksonXmlRootElement(localName = "project")
public class TypescriptProject {

    private List<TypescriptEntity> entities = new ArrayList<>();
    private List<TypescriptDependency> dependencies = new ArrayList<>();
}
