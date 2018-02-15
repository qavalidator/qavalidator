package de.qaware.qav.util;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Util methods to spawn OS processes. This class uses the ExecutorService to spawn processes in the background (if
 * desired).
 * <p>
 * Therefore, it is <b>important to call the method {@link #finish()}</b> before the application is done -- otherwise,
 * the threads in the Thread Pool will wait for jobs, and the JVM process will not terminate!
 *
 * @author tilman
 */
public final class ProcessUtil {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessUtil.class);

    /**
     * size of the thread pool.
     */
    private static final int NUM_OF_THREADS = 4;

    private static ExecutorService threadPool = null;
    private static CompletionService<Integer> cs;

    /**
     * util class with only static methods.
     */
    private ProcessUtil() {
    }

    private static synchronized void init() {
        if (threadPool == null) {
            threadPool = Executors.newFixedThreadPool(NUM_OF_THREADS);
            cs = new ExecutorCompletionService<>(threadPool);
        }
    }

    /**
     * Shut down the Thread Pool for background processes. Call this method at the end of your application! If you
     * don't, the JVM process will not terminate because there are still threads sitting around.
     */
    public static synchronized void finish() {
        if (threadPool != null) {
            threadPool.shutdown();
            if (cs.poll() != null) {
                LOGGER.info("Waiting for background jobs to finish.");
            } else {
                LOGGER.info("All background jobs are done.");
            }
            threadPool = null;
        }
    }

    /**
     * Start the given command in the background, without waiting for the result. If you use this way of starting
     * processes do not forget to call {@link #finish()} at the end of your application, otherwise your JVM won't
     * terminate.
     *
     * @param directory the directory where to start the process
     * @param cmd       the command line
     */
    public static void execProcessInBackground(final String directory, final List<String> cmd) {
        init();
        Callable<Integer> worker = () -> execProcess(directory, cmd);
        cs.submit(worker);
    }

    /**
     * Start the given command synchronously, waiting for it to finish.
     *
     * @param directory the directory where to start the process
     * @param cmd       the command line
     * @return the exit value of the process
     */
    @SuppressWarnings("squid:S1166") // wants log or rethrow exception. It's logged well enough here.
    public static int execProcess(String directory, List<String> cmd) {
        File procDirectory = new File(directory);
        LOGGER.debug("Starting process [dir:{}]: {}", procDirectory.getAbsolutePath(), cmd);

        ProcessBuilder processBuilder = new ProcessBuilder(cmd);
        processBuilder.directory(procDirectory);

        String stdOut = null;
        String stdError = null;
        int result = -1;
        long start = System.currentTimeMillis();
        try {
            Process process = processBuilder.start();
            result = process.waitFor();
            stdOut = IOUtils.toString(process.getInputStream(), Charset.defaultCharset());
            stdError = IOUtils.toString(process.getErrorStream(), Charset.defaultCharset());
        } catch (IOException e) {
            LOGGER.error("Error starting the postprocessing command '{}': {}", cmd.get(0), e.getMessage());
        } catch (InterruptedException e) {
            LOGGER.error("Error postprocessing '{}': {}", cmd.get(0), e.getMessage());
            Thread.currentThread().interrupt();
        }
        long end = System.currentTimeMillis();

        String successString = (result == 0) ? "SUCCESS: " : "FAILURE: ";
        LOGGER.info("{} executed command '{}' in {} ms", successString, cmd, (end - start));

        if (stdOut != null && !stdOut.isEmpty()) {
            LOGGER.info("Stdout: {}", stdOut);
        }
        if (stdError != null && !stdError.isEmpty()) {
            LOGGER.info("Stderr: {}", stdError);
        }

        return result;
    }
}
