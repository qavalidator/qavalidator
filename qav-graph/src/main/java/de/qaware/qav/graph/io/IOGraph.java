package de.qaware.qav.graph.io;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Helper class to hold nodes and edges for exporting and importing.
 *
 * Nodes are represented a s {@link Map} from String to Object; the object may be a String, an Integer, a Boolean,
 * or a {@link List} thereof.
 *
 * @author QAware GmbH
 */
@Data
public class IOGraph {

    private List<Map<String, Object>> nodes = new ArrayList<>();
    private List<IOEdge> edges = new ArrayList<>();

    @Override
    public String toString() {
        return String.format("%s with %d nodes, %d edges", this.getClass().getSimpleName(), nodes.size(), edges.size());
    }
}
