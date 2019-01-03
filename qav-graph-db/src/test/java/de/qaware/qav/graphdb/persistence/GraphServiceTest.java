package de.qaware.qav.graphdb.persistence;

import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.io.GraphReaderWriter;
import de.qaware.qav.graphdb.model.AbstractNode;
import de.qaware.qav.graphdb.model.ArchitectureNode;
import de.qaware.qav.graphdb.model.ClassNode;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.transaction.Transaction;
import org.slf4j.Logger;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Tests for {@link GraphService}
 */
public class GraphServiceTest {

    private static final Logger LOGGER = getLogger(GraphServiceTest.class);

    private static DependencyGraph graph;

    private Session sessionMock;
    private Transaction tx;
    private GraphService graphService;

    @BeforeClass
    public static void setup() {
        graph = GraphReaderWriter.read("src/test/resources/dependencyGraph.json");
    }

    @Before
    public void init() {
        sessionMock = mock(Session.class);
        tx = mock(Transaction.class);
        when(sessionMock.beginTransaction()).thenReturn(tx);

        graphService = new GraphService(sessionMock);
    }

    @Test
    public void save() {
        graphService.saveGraph(graph);

        verify(sessionMock).beginTransaction();
        verify(sessionMock, times(graph.getAllNodes().size())).save(any(AbstractNode.class), eq(1));
        verifyNoMoreInteractions(sessionMock);
        verify(tx).commit();
    }

    @Test
    public void deleteAll() {
        graphService.deleteAll();

        verify(sessionMock).beginTransaction();
        verify(sessionMock).deleteAll(ClassNode.class);
        verify(sessionMock).deleteAll(ArchitectureNode.class);

        verifyNoMoreInteractions(sessionMock);
        verify(tx).commit();
    }

    // @Test
    public void saveAndRead() {
        GraphService service = new GraphService();

        service.deleteAll();
        service.saveGraph(graph);
        Collection<ClassNode> names = service.findByProperty("name", DependencyGraph.class.getCanonicalName());

        assertThat(names).isNotNull();
        assertThat(names).hasSize(1);
        ClassNode node = names.iterator().next();

        LOGGER.info("Class: {}", node);
    }

}
