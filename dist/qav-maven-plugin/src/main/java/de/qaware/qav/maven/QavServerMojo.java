package de.qaware.qav.maven;

import de.qaware.qav.app.server.QavServerConfiguration;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.springframework.boot.SpringApplication;

import java.io.File;

/**
 * Maven Mojo to start the Server.
 * <p>
 * Useful to browse the Dependency Graph, without downloading QAvalidator as a separate standalone tool.
 * <p>
 * Starts the Server, until it is killed with Ctrl-C.
 *
 * @author QAware GmbH
 */
@Mojo(name = "server", aggregator = true)
public class QavServerMojo extends AbstractMojo {

    @Parameter(property = "server.graph", defaultValue = "target/qav-report/dependencyGraph.json")
    private String graph;

    @Parameter(property = "server.port", defaultValue = "8080")
    private String port;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Port: " + port + "; graph: " + graph);
        getLog().info("Go to http://localhost:" + port + "/");
        getLog().info("Hit Ctrl-C to stop.");

        checkFileAvailable(graph);
        System.setProperty("graph", graph);
        System.setProperty("server.port", port);
        SpringApplication.run(QavServerConfiguration.class);

        waitForKill();
    }

    public void setGraph(String graph) {
        this.graph = graph;
    }

    public void setPort(String port) {
        this.port = port;
    }

    private void checkFileAvailable(String graphFilename) throws MojoFailureException {
        File graphFile = new File(graphFilename);
        if (!graphFile.exists()) {
            String msg = "File " + graphFile.getAbsolutePath() + " not found.";
            getLog().error(msg);
            throw new MojoFailureException(msg);
        }
    }

    /**
     * Wait until stopped with Ctrl-C
     */
    private void waitForKill() {
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            getLog().error("Interrupted", e);
        }
    }
}
