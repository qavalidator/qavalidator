package de.qaware.qav.analysis.plugins.analysis

import de.qaware.qav.analysis.plugins.base.BasePlugin
import de.qaware.qav.doc.QavCommand
import de.qaware.qav.doc.QavPluginDoc
import de.qaware.qav.analysis.dsl.model.Analysis
import de.qaware.qav.graph.api.DependencyGraph
import de.qaware.qav.graph.api.DependencyType
import de.qaware.qav.graph.api.NodeFilter
import de.qaware.qav.graph.filter.*
import groovy.util.logging.Slf4j

/**
 * QAV Language to deal with filters.
 * Create, manage, find, and combine filters.
 *
 * @author QAware GmbH
 */
@QavPluginDoc(name = "GraphFilterQavPlugin",
        description = """
        A graph can be filtered according to properties on the nodes or on the dependencies.
        Filtered graphs can then be used for further analysis (see <<qav-analysis-dsl-validation>>),
        or they can be exported for visualization (see <<qav-analysis-dsl-export>>).

        The GraphFilterQavPlugin offers are a number of filters on nodes and on edges.
        Filters can be combined in the expected ways, using combinations with `and` and `or`, and negation with `not`.
        """
)
@Slf4j
class GraphFilterQavPlugin extends BasePlugin {

    private Map<String, NodeFilter> filters = [:]
    private DependencyGraph dependencyGraph

    @Override
    void apply(Analysis analysis) {
        super.apply(analysis)

        this.dependencyGraph = analysis.context.dependencyGraph

        analysis.register("addFilter", this.&addFilter)
        analysis.register("filter", this.&getFilter)

        analysis.register("propertyExistsFilter", this.&propertyExistsFilter)
        analysis.register("propertyInFilter", this.&propertyInFilter)
        analysis.register("nodeNameOutFilter", this.&nodeNameOutFilter)
        analysis.register("dependencyTypeNodeFilter", this.&dependencyTypeNodeFilter)
        analysis.register("outgoingDependencyTypeFilter", this.&outgoingDependencyTypeFilter)

        analysis.register("dependencyTypeEdgeOutFilter", this.&dependencyTypeEdgeOutFilter)
        analysis.register("dependencyPropertyInFilter", this.&dependencyPropertyInFilter)
        analysis.register("dependencyPropertyExistsFilter", this.&dependencyPropertyExistsFilter)

        analysis.register("and", this.&andFilter)
        analysis.register("or", this.&orFilter)
        analysis.register("not", this.&notFilter)
    }

    /**
     * Registers a new filter under the given name.
     *
     * @param name the name
     * @param filter the filter
     * @return the filter.
     */
    @QavCommand(
            name = "addFilter",
            description = "Registers a new filter under the given name.",
            parameters = [
                    @QavCommand.Param(name = "name", description = "the name"),
                    @QavCommand.Param(name = "filter", description = "the filter")
            ],
            result = "Returns the filter."
    )
    NodeFilter addFilter(String name, NodeFilter filter) {
        this.filters[name] = filter
    }

    /**
     * Returns the filter with the given name. See {@link #addFilter(java.lang.String, NodeFilter)}.
     * Reports an error (resulting in a {@link IllegalStateException}) if the filter does not exist.
     *
     * @param name the name
     * @return the filter registered under that name.
     * @throws IllegalStateException if the filter does not exist
     */
    @QavCommand(name = "filter",
            description = "Returns the filter with the given name.",
            parameters = @QavCommand.Param(name = "name", description = "the name"),
            result = "The filter registered under that name, or null if no such filter is available."
    )
    NodeFilter getFilter(String name) {
        def filter = this.filters[name]
        if (!filter) {
            analysis.error("Filter ${name} not found.")
        }
        return filter
    }

    /**
     * Creates a new {@link de.qaware.qav.graph.filter.NodePropertyExistsFilter}.
     * This is an "IN" filter.
     *
     * @param propertyName the name of the property which must exist to accept the node
     * @return The new filter
     */
    @QavCommand(name = "propertyExistsFilter",
            description = "Creates a new {@link NodePropertyExistsFilter}. This is an _IN_ filter.",
            parameters = @QavCommand.Param(name = "propertyName", description = "The name of the property which must exist to accept the node."),
            result = "The new {@link NodePropertyExistsFilter}."
    )
    static NodePropertyExistsFilter propertyExistsFilter(String propertyName) {
        return new NodePropertyExistsFilter(propertyName)
    }

    /**
     * Creates a new {@link de.qaware.qav.graph.filter.NodePropertyInFilter}.
     * This is an "IN" filter.
     *
     * @param propertyName the property name
     * @param value the value that must match to accept the node
     * @return The new filter.
     */
    @QavCommand(name = "propertyInFilter",
            description = "Creates a new {@link NodePropertyInFilter}.",
            parameters = [
                    @QavCommand.Param(name = "propertyName", description = "The property name"),
                    @QavCommand.Param(name = "value", description = "The value that must match to accept the node.")],
            result = "The new {@link NodePropertyInFilter}."
    )
    static NodePropertyInFilter propertyInFilter(String propertyName, Object value) {
        return new NodePropertyInFilter(propertyName, value)
    }

    /**
     * Creates a new <tt>NodeNameOutFilter</tt>.
     * This is an "OUT" filter.
     * Only if none of the given patterns matches the name, the node is accepted.
     *
     * @param patterns The patterns to filter out.
     * @return The new filter.
     */
    @QavCommand(name = "nodeNameOutFilter",
            description = "Creates a new `NodeNameOutFilter`.",
            parameters = @QavCommand.Param(name = "patterns...", description = """
                    The patterns (Ant path style) to filter out. 
                    Only if none of the given patterns matches the name, the node is accepted.
                    """),
            result = "The new `NodeNameOutFilter`."
    )
    static NodeFilter nodeNameOutFilter(String... patterns) {
        return new NotFilter(new NodeNameInFilter(patterns))
    }

    /**
     * Creates a new {@link de.qaware.qav.graph.filter.DependencyTypeNodeFilter}.
     * This is an "IN" filter.
     *
     * @param dependencyTypeNames The dependency type names to accept in the filter
     * @return The new filter.
     */
    @QavCommand(name = "dependencyTypeNodeFilter",
            description = "Creates a new {@link DependencyTypeNodeFilter}. This is an _IN_ filter.",
            parameters = @QavCommand.Param(name = "dependencyTypeName", description = "The dependency type name to accept in the filter."),
            result = "The new {@link DependencyTypeNodeFilter}."
    )
    DependencyTypeNodeFilter dependencyTypeNodeFilter(String dependencyTypeName) {
        return new DependencyTypeNodeFilter(dependencyGraph, DependencyType.valueOf(dependencyTypeName))
    }

    /**
     * Creates a new {@link DependencyTypeNodeFilter}.
     * This is an "IN" filter.
     *
     * @param dependencyTypes The dependency types to accept in the filter
     * @return the new filter
     */
    @QavCommand(name = "dependencyTypeNodeFilter",
            description = "Creates a new {@link DependencyTypeNodeFilter}. This is an _IN_ filter.",
            parameters = @QavCommand.Param(name = "dependencyType", description = "The dependency type to accept in the filter."),
            result = "The new {@link DependencyTypeNodeFilter}."
    )
    DependencyTypeNodeFilter dependencyTypeNodeFilter(DependencyType dependencyType) {
        return new DependencyTypeNodeFilter(dependencyGraph, dependencyType)
    }

    /**
     * Creates a new {@link de.qaware.qav.graph.filter.OutgoingDependencyTypeFilter}.
     * This is an "IN" filter.
     *
     * @param dependencyTypeName The dependency type name to accept in the filter
     * @return The new filter.
     */
    @QavCommand(name = "outgoingDependencyTypeFilter",
            description = "Creates a new {@link OutgoingDependencyTypeFilter}. This is an _IN_ filter.",
            parameters = @QavCommand.Param(name = "dependencyTypeName", description = "The dependency type name to accept in the filter."),
            result = "The new {@link OutgoingDependencyTypeFilter}."
    )
    OutgoingDependencyTypeFilter outgoingDependencyTypeFilter(String dependencyTypeName) {
        return new OutgoingDependencyTypeFilter(dependencyGraph, DependencyType.valueOf(dependencyTypeName))
    }

    /**
     * Creates a new {@link OutgoingDependencyTypeFilter}.
     * This is an "IN" filter.
     *
     * @param dependencyType The dependency type to accept in the filter
     * @return the new filter
     */
    @QavCommand(name = "outgoingDependencyTypeFilter",
            description = "Creates a new {@link OutgoingDependencyTypeFilter}. This is an _IN_ filter.",
            parameters = @QavCommand.Param(name = "dependencyType", description = "The dependency type to accept in the filter."),
            result = "The new {@link OutgoingDependencyTypeFilter}."
    )
    OutgoingDependencyTypeFilter outgoingDependencyTypeFilter(DependencyType dependencyTypes) {
        return new OutgoingDependencyTypeFilter(dependencyGraph, dependencyTypes)
    }

    /**
     * Creates a new {@link de.qaware.qav.graph.filter.DependencyTypeEdgeOutFilter}.
     * This is an "OUT" filter.
     *
     * @param dependencyTypeNames The dependency type names to filter out.
     * @return The new filter.
     */
    @QavCommand(name = "dependencyTypeEdgeOutFilter",
            description = "Creates a new {@link DependencyTypeEdgeOutFilter} to filter out _dependencies_.",
            parameters = @QavCommand.Param(name = "dependencyTypeNames...", description = "The dependency type names to filter out."),
            result = "The new {@link DependencyTypeEdgeOutFilter}."
    )
    static DependencyTypeEdgeOutFilter dependencyTypeEdgeOutFilter(String... dependencyTypeNames) {
        def dependencyTypes = []
        dependencyTypeNames.each {
            dependencyTypes << DependencyType.valueOf(it)
        }
        return new DependencyTypeEdgeOutFilter(dependencyTypes)
    }

    /**
     * Creates a new {@link DependencyTypeEdgeOutFilter}.
     * This is an "OUT" filter.
     *
     * @param dependencyTypes The dependency types to filter out.
     * @return the new filter
     */
    @QavCommand(name = "dependencyTypeEdgeOutFilter",
            description = "Creates a new {@link DependencyTypeEdgeOutFilter} to filter out _dependencies_.",
            parameters = @QavCommand.Param(name = "dependencyTypes...", description = "The dependency types to filter out."),
            result = "The new {@link DependencyTypeEdgeOutFilter}."
    )
    static DependencyTypeEdgeOutFilter dependencyTypeEdgeOutFilter(DependencyType... dependencyTypes) {
        return new DependencyTypeEdgeOutFilter(dependencyTypes)
    }

    /**
     * Creates a new {@link de.qaware.qav.graph.filter.DependencyPropertyInFilter} to filter dependencies.
     * This is an "IN" filter.
     *
     * @param propertyName the property name
     * @param value the value that must match to accept the dependency
     * @return the new filter
     */
    @QavCommand(name = "dependencyPropertyInFilter",
            description = "Creates a new {@link DependencyPropertyInFilter} to filter which _dependencies_ to keep.",
            parameters = [
                    @QavCommand.Param(name = "propertyName", description = "The property name"),
                    @QavCommand.Param(name = "value", description = "The value that must be there to accept the edge.")],
            result = "The new {@link DependencyPropertyInFilter}."
    )
    static DependencyPropertyInFilter dependencyPropertyInFilter(String propertyName, Object value) {
        return new DependencyPropertyInFilter(propertyName, value)
    }

    /**
     * Creates a new {@link de.qaware.qav.graph.filter.DependencyPropertyExistsFilter} to filter dependencies.
     * This is an "IN" filter.
     *
     * @param propertyName The property name
     * @return The new filter.
     */
    @QavCommand(name = "dependencyPropertyExistsFilter",
            description = "Creates a new {@link DependencyPropertyExistsFilter} to filter which _dependencies_ to keep.",
            parameters = @QavCommand.Param(name = "propertyName",
                    description = "The property name which must be set on the dependency. The value does not matter."),
            result = "The new {@link DependencyPropertyExistsFilter}."
    )
    static DependencyPropertyExistsFilter dependencyPropertyExistsFilter(String propertyName) {
        return new DependencyPropertyExistsFilter(propertyName)
    }

    /**
     * Creates a new {@link de.qaware.qav.graph.filter.AndFilter}.
     *
     * @param filters the filters to combine with AND
     * @return the new filter
     */
    @QavCommand(name = "and",
            description = "Creates a new {@link AndFilter}.",
            parameters = @QavCommand.Param(name = "filters...", description = "The filters to combine with AND"),
            result = "A new {@link AndFilter}"
    )
    static AndFilter andFilter(NodeFilter... filters) {
        return new AndFilter(filters)
    }

    /**
     * Creates a new {@link de.qaware.qav.graph.filter.OrFilter}.
     *
     * @param filters the filters to combine with OR
     * @return the new filter
     */
    @QavCommand(name = "or",
            description = "Creates a new {@link OrFilter}.",
            parameters = @QavCommand.Param(name = "filters...", description = "The filters to combine with OR"),
            result = "A new {@link OrFilter}"
    )
    static OrFilter orFilter(NodeFilter... filters) {
        return new OrFilter(filters)
    }

    /**
     * Creates a new {@link NotFilter}.
     *
     * @param filter the filter to negate
     * @return the new filter
     */
    @QavCommand(name = "not",
            description = "Creates a new {@link NotFilter}.",
            parameters = @QavCommand.Param(name = "filter", description = "The filter to negate"),
            result = "A new {@link NotFilter}"
    )
    static NotFilter notFilter(NodeFilter filter) {
        return new NotFilter(filter)
    }
}
