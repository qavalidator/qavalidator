package de.qaware.qav.graph.io;

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
public class IOEdge {

    private String from;
    private String to;
    private String type;
    private Map<String, Object> props = new HashMap<>();

    private List<IOEdge> baseDependencies = new ArrayList<>();

    // --- getters and setters

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getProps() {
        return props;
    }

    public void setProps(Map<String, Object> props) {
        this.props = props;
    }

    public List<IOEdge> getBaseDependencies() {
        return baseDependencies;
    }

    public void setBaseDependencies(List<IOEdge> baseDependencies) {
        this.baseDependencies = baseDependencies;
    }

    @Override
    public String toString() {
        return from + " --[" + type + "]--> " + to;
    }
}
