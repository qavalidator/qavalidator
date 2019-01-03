package de.qaware.qav.graphdb.persistence;

import com.google.common.base.Stopwatch;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graphdb.model.ArchitectureNode;
import de.qaware.qav.graphdb.model.ClassNode;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.cypher.ComparisonOperator;
import org.neo4j.ogm.cypher.Filter;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.neo4j.ogm.transaction.Transaction;

import java.util.Collection;

/**
 * Service layer to read and write graphs into/from Neo4j.
 */
@Slf4j
public class GraphService {

    private Session session;

    /**
     * Constructor.
     */
    public GraphService() {
        Configuration configuration = Neo4jConfiguration.getConfiguration();

        SessionFactory sessionFactory = new SessionFactory(configuration, "de.qaware.qav.graphdb.model");

        this.session = sessionFactory.openSession();
    }

    /**
     * Constructor.
     * <p>
     * Get the {@link Session}. Useful for testing.
     *
     * @param session the {@link Session}
     */
    public GraphService(Session session) {
        this.session = session;
    }

    /**
     * Saves the complete graph into the Neo4j DB. Does not check for existing nodes.
     *
     * @param dependencyGraph the {@link DependencyGraph}
     */
    public void saveGraph(DependencyGraph dependencyGraph) {
        LOGGER.info("Saving graph ...");
        Stopwatch stopwatch = Stopwatch.createStarted();
        Transaction tx = session.beginTransaction();

        GraphMapper mapper = new GraphMapper();
        mapper.toNeo4j(dependencyGraph);
        mapper.getNodes().forEach(n -> session.save(n, 1));

        tx.commit();
        stopwatch.stop();

        LOGGER.info("... done in {}: {} nodes and {} edges.", stopwatch.toString(),
                mapper.getNodes().size(), mapper.getReferencesRelations().size());
    }

    /**
     * Deletes all nodes of type {@link ClassNode} and {@link ArchitectureNode}.
     */
    public void deleteAll() {
        LOGGER.info("Delete all nodes");
        Transaction tx = session.beginTransaction();

        session.deleteAll(ClassNode.class);
        session.deleteAll(ArchitectureNode.class);

        tx.commit();
    }

    /**
     * Finds all nodes where the given property equals the given value.
     *
     * @param key   the property name
     * @param value the value
     * @return all nodes matching the criteria
     */
    public Collection<ClassNode> findByProperty(String key, Object value) {
        Filter filter = new Filter(key, ComparisonOperator.EQUALS, value);
        return findByFilter(filter);
    }

    /**
     * Finds all nodes where the given filter applies.
     *
     * @param filter the {@link Filter}
     * @return all nodes matching the criteria
     */
    public Collection<ClassNode> findByFilter(Filter filter) {
        return session.loadAll(ClassNode.class, filter);
    }
}
