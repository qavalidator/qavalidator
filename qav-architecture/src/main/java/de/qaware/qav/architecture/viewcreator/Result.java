package de.qaware.qav.architecture.viewcreator;

import de.qaware.qav.graph.api.DependencyGraph;
import lombok.Data;

/**
 * Bean to transport two result: a {@link DependencyGraph} and possibly an error message.
 *
 * @author QAware GmbH
 */
@Data
public class Result {
    private String violationMessage;
    private DependencyGraph architectureGraph;
}

