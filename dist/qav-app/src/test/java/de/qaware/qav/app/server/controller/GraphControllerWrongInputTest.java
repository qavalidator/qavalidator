package de.qaware.qav.app.server.controller;

import org.junit.Test;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Tests for {@link GraphController}.
 *
 * Here is only for testing the behaviour with wrong input.
 * See also {@link GraphControllerTest}.
 *
 * @author QAware GmbH
 */
public class GraphControllerWrongInputTest {

    @Test
    public void testGraphControllerWrongInput() {
        GraphController graphController = new GraphController();
        graphController.setFilename("/not/existing/graph");

        try {
            graphController.init();
            fail("IllegalArgumentException expected");
        } catch(IllegalArgumentException e) {
            assertThat(e.getMessage(), startsWith("File not found:"));
            assertThat(e.getMessage().replaceAll("\\\\", "/"), endsWith("/not/existing/graph"));
        }
    }
}
