package de.qaware.qav.graph.filter;

import com.google.common.collect.Lists;
import de.qaware.qav.graph.api.Node;
import de.qaware.qav.graph.api.NodeFilter;
import de.qaware.qav.util.QavNameMatcher;

import java.util.Collections;
import java.util.List;

/**
 * This filter is to find nodes on the basis of their name.
 * <p>
 * Only if at least one of the given patterns match the name, the node is accepted.
 * Otherwise, the node is filtered away.
 * The patterns are Ant path style.
 *
 * @author QAware GmbH
 */
public class NodeNameInFilter implements NodeFilter {

    private final List<String> wildcards = Lists.newArrayList();
    private final QavNameMatcher nameMatcher = new QavNameMatcher();

    /**
     * Value constructor.
     * <p>
     * Only if at least one of the given patterns match the name, the node is accepted.
     * Otherwise, the node is filtered away.
     * The patterns are Ant path style.
     *
     * @param patterns the patterns
     */
    public NodeNameInFilter(String... patterns) {
        addFilter(patterns);
    }

    /**
     * Only if at least one of the given patterns match the name, the node is accepted.
     * Otherwise, the node is filtered away.
     * The patterns are Ant path style.
     *
     * @param patterns the patterns
     * @return <tt>this</tt>
     */
    public final NodeNameInFilter addFilter(String... patterns) {
        Collections.addAll(wildcards, patterns);
        return this;
    }

    @Override
    public boolean isAccepted(Node node) {
        return wildcards.stream().anyMatch(p -> nameMatcher.matches(p, node.getName()));
    }
}
