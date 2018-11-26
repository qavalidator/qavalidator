package de.qaware.qav.app;

import de.qaware.qav.server.QavServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

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
@Slf4j
public class QavMain {

    /**
     * Main method.
     * <p>
     * Run the analysis, the web application, or both.
     *
     * @param args command line
     */
    public static void main(String... args) {
        boolean runAnalysis = hasArgument("analysis", args) || hasArgument("outputDir", args);
        boolean startServer = hasArgument("graph", args) || hasArgument("de.qaware.qav.graph.filename", args);

        if (runAnalysis || !startServer) {
            LOGGER.info("Run analysis");
            new SpringApplicationBuilder(QavMain.class)
                    .web(WebApplicationType.NONE) // don't start the embedded Tomcat for the analysis batch run
                    .run(args) // run the analysis
                    .close(); // and finish when done.
        }

        if (startServer) {
            LOGGER.info("Run web application");
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
