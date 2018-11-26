package de.qaware.qav.server.controller;

import de.qaware.qav.graph.api.Node;
import de.qaware.qav.server.exceptions.NotFoundException;
import de.qaware.qav.server.model.DependencyDTO;
import de.qaware.qav.server.model.NodeDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link GraphController}.
 * <p>
 * See also {@link GraphControllerWrongInputTest} for tests with wrong input.
 *
 * @author QAware GmbH
 */
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GraphControllerTest {

    @Autowired
    private GraphController graphController;

    @Test
    public void testHello() {
        assertThat(graphController.getInfo().getInfo(), startsWith("Graph with: 5 nodes and 3 edges. Filename: "));
        assertThat(graphController.getInfo().getInfo(), endsWith("testGraph1.json"));
    }

    @Test
    public void testGetNode() {
        NodeDTO node = graphController.getNode("v1");
        assertThat(node, notNullValue());
        assertThat(node.getProperties().get("KEY"), is(5));
    }

    @Test(expected = NotFoundException.class)
    public void testGetNodeNotExistent() {
        graphController.getNode("not-existent");
    }

    @Test
    public void testGetEdge() {
        DependencyDTO edge = graphController.getEdge("v4", "v5");
        assertThat(edge, notNullValue());
        assertThat(edge.getSourceName(), is("v4"));
        assertThat(edge.getTargetName(), is("v5"));
        assertThat(edge.getTypeName(), is("READ_WRITE"));
    }

    @Test(expected = NotFoundException.class)
    public void testGetEdgeWhenSourceNodeIsWrong() {
        graphController.getEdge("not-existing-node", "v5");
    }

    @Test(expected = NotFoundException.class)
    public void testGetEdgeWhenTargetNodesIsWrong() {
        graphController.getEdge("v4", "not-existing-node");
    }

    @Test(expected = NotFoundException.class)
    public void testGetEdgeWhenNotFound() {
        graphController.getEdge("v1", "v5");
    }


    @Test
    public void testGetNodes() {
        Page<Node> resultPage = graphController.getNodes("name:v*", PageRequest.of(0, 30));
        assertThat(resultPage, notNullValue());
        assertThat(resultPage.getTotalElements(), is(5L));
        assertThat(resultPage.getTotalPages(), is(1));

        List<Node> nodes = resultPage.getContent();
        assertThat(nodes, hasSize(5));
    }

    @Test
    public void testGetNodesMorePages() {
        Page<Node> resultPage = graphController.getNodes("name:v*", PageRequest.of(0, 3));

        // page 1:
        assertThat(resultPage, notNullValue());
        assertThat(resultPage.getTotalElements(), is(5L));
        assertThat(resultPage.getTotalPages(), is(2));
        assertThat(resultPage.getNumber(), is(0));

        List<Node> nodes = resultPage.getContent();
        assertThat(nodes, hasSize(3));

        // page 2:
        resultPage = graphController.getNodes("name:v*", PageRequest.of(1, 3));
        assertThat(resultPage, notNullValue());
        assertThat(resultPage.getTotalElements(), is(5L));
        assertThat(resultPage.getTotalPages(), is(2));
        assertThat(resultPage.getNumber(), is(1));

        nodes = resultPage.getContent();
        assertThat(nodes, hasSize(2));
    }

    @Test
    public void testGetNodesNotFound() {
        Page<Node> resultPage = graphController.getNodes("name:xx*", PageRequest.of(0, 3));

        // page 1:
        assertThat(resultPage, notNullValue());
        assertThat(resultPage.getTotalElements(), is(0L));
        assertThat(resultPage.getTotalPages(), is(0));

        List<Node> nodes = resultPage.getContent();
        assertThat(nodes, hasSize(0));
    }

    @Test
    public void testGetNodesBehindLastPage() {
        Page<Node> resultPage = graphController.getNodes("name:v*", PageRequest.of(1, 30));

        // page 1:
        assertThat(resultPage, notNullValue());
        assertThat(resultPage.getTotalElements(), is(5L));
        assertThat(resultPage.getTotalPages(), is(1));
        assertThat(resultPage.getNumber(), is(1));

        List<Node> nodes = resultPage.getContent();
        assertThat(nodes, hasSize(0));
    }

    @Test
    public void testGetNodesNoQuery() {
        Page<Node> resultPage = graphController.getNodes(null, PageRequest.of(0, 30));
        assertThat(resultPage, notNullValue());
        assertThat(resultPage.getTotalElements(), is(5L));
        assertThat(resultPage.getTotalPages(), is(1));

        List<Node> nodes = resultPage.getContent();
        assertThat(nodes, hasSize(5));
    }

    @Test
    public void testGetNodesWithOnlyName() {
        Page<Node> resultPage = graphController.getNodes("v*", PageRequest.of(0, 30));
        assertThat(resultPage, notNullValue());
        assertThat(resultPage.getTotalElements(), is(5L));
        assertThat(resultPage.getTotalPages(), is(1));

        List<Node> nodes = resultPage.getContent();
        assertThat(nodes, hasSize(5));
    }
}
