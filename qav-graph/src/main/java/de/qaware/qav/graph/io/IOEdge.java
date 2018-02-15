package de.qaware.qav.graph.io;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Edge in an {@link IOGraph}.
 *
 * Knows the source and target node names, the type,
 * carries the property map,
 * and knows about the <tt>baseRelations</tt>.
 *
 * @author QAware GmbH
 */
@Data
public class IOEdge {

    private String from;
    private String to;
    private String type;
    private Map<String, Object> props = new HashMap<>();

    private List<IOEdge> baseDependencies = new ArrayList<>();

    @Override
    public String toString() {
        return from + " --[" + type + "]--> " + to;
    }
}
