package de.qaware.qav.app;

import de.qaware.qav.server.QavServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class.
 * <p>
 * Start QAvalidator from command line.
 * <p>
 * Uses the Spring Boot mechanism to parse the command line. I.e. override the default values with
 * <tt>--analysis=my/analysis/file.groovy</tt> etc.
 *
 * @author QAware GmbH
 */
@SpringBootApplication
public class QavMain {

    /**
     * property name to set the server port.
     */
    private static final String SERVER_PORT = "server.port";

    /**
     * Main method.
     *
     * @param args command line
     */
    public static void main(String... args) {
        boolean runAnalysis = hasArgument("analysis", args) || hasArgument("outputDir", args);
        boolean startServer = hasArgument("graph", args) || hasArgument("de.qaware.qav.graph.filename", args);

        if (runAnalysis || !startServer) {
            String port = System.getProperty(SERVER_PORT); // may be null
            System.setProperty(SERVER_PORT, "0");
            SpringApplication.run(QavMain.class, args).close();
            if (port == null) {
                System.clearProperty(SERVER_PORT);
            } else {
                System.setProperty(SERVER_PORT, port);
            }
        }

        if (startServer) {
            SpringApplication.run(QavServer.class, args);
        }
    }

    private static boolean hasArgument(String arg, String[] args) {
        for (String a : args) {
            if (a.startsWith("--" + arg + "=")) {
                return true;
            }
        }
        return false;
    }

}
