package de.qaware.qav.analysis.dsl.impl

import groovy.util.logging.Slf4j

/**
 * Runs the analysis jobs configured in the DSL.
 *
 * Analysis jobs are just closures; they can use the common "context" to pass around intermediate results.
 *
 * @author QAware GmbH
 */
@Slf4j
class QavAnalysisRunner {

    List<String> failedSteps = []

    private Map<String, Closure> analysisJobs
    private Expando context

    /**
     * Constructor.
     *
     * @param analysisJobs the analysis jobs
     * @param context the context of the analysis run
     */
    QavAnalysisRunner(Map<String, Closure> analysisJobs, Expando context) {
        this.analysisJobs = analysisJobs
        this.context = context
    }

    /**
     * Runs each job.
     *
     * If any of them failed with an error, logs all errors (again) in the end.
     */
    void runAnalysisJobs() {
        analysisJobs.each {String name, Closure closure ->
            doAnalysis(name, closure)
        }

        if (failedSteps) {
            log.error "=== Steps with errors: ==="
            failedSteps.each {
                log.error "  * ${it}"
            }
        }

        context.failedSteps = failedSteps
    }

    /**
     * Runs an analysis job.
     *
     * Catches all Throwables and logs them as errors.
     * This way, the analysis run can continue -- if that is possible, i.e if later steps don't rely on
     * possibly missing results.
     *
     * @param name the name of the job
     * @param c the Closure to run
     */
    private void doAnalysis(String name, Closure c) {
        log.info("===== Start ${name} =====")
        long start = System.currentTimeMillis()

        boolean success = false
        getViolations().clear()
        c.setDelegate(context)

        try {
            c.call()
            success = true
        } catch (Throwable e) {
            log.error("Error: ${e}", e)
        } finally {
            long duration = System.currentTimeMillis() - start
            if (getViolations()) {
                success = false
            }
            String result = "SUCCESS"
            if (!success) {
                result = "FAILED"
                failedSteps << name
            }
            log.info("===== ${result} (${duration} ms): ${name} =====")
        }
    }

    private List<String> getViolations() {
        return this.context.violations
    }
}
