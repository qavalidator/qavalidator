package de.qaware.qav.architecture.viewcreator;

import de.qaware.qav.graph.api.DependencyGraph;
import lombok.Data;

/**
 * Bean to transport two results: a {@link DependencyGraph} and possibly an error message.
 *
 * @author QAware GmbH
 */
@Data
public class Result {

    /** always given. */
    private DependencyGraph architectureGraph;

    /** may be null if there is no violation. */
    private String violationMessage;
}

