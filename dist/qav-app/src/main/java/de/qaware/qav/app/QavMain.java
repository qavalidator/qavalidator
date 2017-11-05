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
     * Main method.
     *
     * @param args command line
     */
    public static void main(String... args) {
        boolean runAnalysis = hasArgument("analysis", args) || hasArgument("outputDir", args);
        boolean startServer = hasArgument("graph", args) || hasArgument("de.qaware.qav.graph.filename", args);

        if (runAnalysis || !startServer) {
            String port = System.getProperty("server.port"); // may be null
            System.setProperty("server.port", "0");
            SpringApplication.run(QavMain.class, args).close();
            if (port == null) {
                System.clearProperty("server.port");
            } else {
                System.setProperty("server.port", port);
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
