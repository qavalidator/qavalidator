package de.qaware.qav.graph.api;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.util.List;

import static java.lang.String.format;

/**
 * Represents a Dependency (Edge) in a Dependency Graph.
 *
 * @author QAware GmbH
 */
public class Dependency extends AbstractGraphElement {

    private final Node source;
    private final Node target;
    private final List<Dependency> baseDependencies = Lists.newArrayList();
    private DependencyType dependencyType;

    /**
     * Value constructor.
     *
     * @param source         the source {@link Node}
     * @param target         the target {@link Node}
     * @param dependencyType the {@link DependencyType}
     */
    public Dependency(Node source, Node target, DependencyType dependencyType) {
        this.source = Preconditions.checkNotNull(source, "Source may not be null");
        this.target = Preconditions.checkNotNull(target, "Target may not be null");
        this.dependencyType = Preconditions.checkNotNull(dependencyType, "Dependency type may not be null");
    }

    public Node getSource() {
        return source;
    }

    public Node getTarget() {
        return target;
    }

    public DependencyType getDependencyType() {
        return dependencyType;
    }

    /**
     * setter.
     *
     * @param dependencyType the {@link DependencyType}, may not be null
     */
    public void setDependencyType(DependencyType dependencyType) {
        this.dependencyType = Preconditions.checkNotNull(dependencyType, "Dependency type may not be null");
    }

    /**
     * adds the given dependency to the {@link #baseDependencies} list, if it is not part of if yet.
     *
     * @param baseDep base dependency
     */
    public void addBaseDependency(Dependency baseDep) {
        if (!baseDependencies.contains(baseDep)) {
            this.baseDependencies.add(baseDep);
        }
    }

    /**
     * getter. returns a copy of the baseDependencies list.
     *
     * @return a copy of the baseDependencies list.
     */
    public List<Dependency> getBaseDependencies() {
        return Lists.newArrayList(baseDependencies);
    }

    @Override
    public String toString() {
        return format("%s --[%s]--> %s", source.getName(), dependencyType.name(), target.getName());
    }
}
