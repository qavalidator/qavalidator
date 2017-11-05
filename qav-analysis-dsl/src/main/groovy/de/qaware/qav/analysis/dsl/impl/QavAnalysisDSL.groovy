package de.qaware.qav.analysis.dsl.impl

import de.qaware.qav.analysis.dsl.model.Analysis
import de.qaware.qav.analysis.dsl.model.QavPlugin
import groovy.util.logging.Slf4j
import org.codehaus.groovy.control.CompilationFailedException

/**
 * QAvalidator Analysis DSL: This is the base class for the Analysis DSL, i.e. it defines the language/the vocabulary of
 * the QAvalidator Analysis DSL.
 *
 * @author QAware GmbH
 */
@Slf4j
abstract class QavAnalysisDSL extends Script implements Analysis {

    // ===== basic methods

    void initScript() {
        this.binding.start = System.currentTimeMillis()
        this.binding.closureMap = [:]
        this.binding.analysisJobs = [:]
        this.binding.context.violations = []
    }

    void finishScript() {
        QavAnalysisRunner runner = new QavAnalysisRunner(this.binding.analysisJobs, context)
        runner.runAnalysisJobs()
        long duration = System.currentTimeMillis() - this.binding.start

        String success = runner.failedSteps.isEmpty() ? "SUCCESS" : "FAILED"
        log.info "${success}: ${duration} ms."
    }

    // =====
    // ===== This is the hard-coded part of the QAvalidator Analysis DSL:
    // =====

    /**
     * Apply the given plugin. The plugin will register its methods via the {@link #register(java.lang.String, groovy.lang.Closure)}
     * method.
     *
     * @param plugin {@link QavPlugin} to apply
     */
    void apply(QavPlugin plugin) {
        plugin.apply(this)
    }

    /**
     * Applies the specified plugin to the DSL.
     *
     * @param pluginName either full class name if plugin is in the QAvalidator project
     * (e.g. "de.qaware.qav.analysis.plugins.ShortcutQavPlugin") or full class file (e.g. "u:/codebase/NewQavPlugin.groovy").
     * Class MUST derive from {@link de.qaware.qav.analysis.dsl.model.QavPlugin}, otherwise it won't work.
     */
    void apply(String pluginName) {
        if (pluginName == "de.qaware.qav.core.analysis.dsl.plugins.ShortcutQavPlugin") {
            pluginName = "de.qaware.qav.analysis.plugins.ShortcutQavPlugin"
            log.warn("ShortcutQavPlugin has moved: It's now ${pluginName}")
        }
        try {
            // Try load internal class
            ClassLoader classLoader = this.getClass().getClassLoader()
            QavPlugin pluginObject = classLoader.loadClass(pluginName).newInstance() as QavPlugin
            pluginObject.apply(this)
        } catch (ClassNotFoundException e1) {
            try {
                // Try load and parse external class
                GroovyClassLoader groovyClassLoader = new GroovyClassLoader()
                Class pluginClass = groovyClassLoader.parseClass(new File(pluginName))
                QavPlugin pluginObject = pluginClass.newInstance() as QavPlugin
                pluginObject.apply(this)
            } catch (CompilationFailedException | IOException e2) {
                error("Could not load plugin ${pluginName}. Further analysis may fail. Cause: ${e2}")
            } catch(IllegalAccessException | InstantiationException e2) {
                error("Could not instantiate plugin ${pluginName}. Further analysis may fail. Cause: ${e2}")
            }
        }
    }

    /**
     * Registers the analysis closure under the given name.
     *
     * @param name name of the job
     * @param closure the job
     * @return the registered closure.
     */
    void analysis(String name, Closure closure) {
        log.debug "Registering job ${name}"
        this.binding.analysisJobs.put name, closure
    }

    /**
     * Reports an error by throwing an {@link IllegalStateException}.
     *
     * @param msg the message to throw
     * @throws IllegalStateException with the given messages
     */
    static void error(String msg) {
        throw new IllegalStateException(msg)
    }

    static void error(Throwable throwable) {
        throw new IllegalStateException(throwable)
    }
    /**
     * Reports a violation by noting it in the context of the analysis.
     * Does not throw an exception, so that the analysis can go on.
     *
     * @param msg the message
     */
    void violation(String msg) {
        log.error(msg)
        getViolations().add(msg)
    }

    // ===== dynamic / plugin stuff

    @Override
    Object invokeMethod(String name, Object args) {
        Closure c = this.binding.closureMap[name] as Closure

        if (c != null) {
            log.debug("invoke closure: {} ({})", name, args)
            return c.call(args)
        } else {
            log.debug("closure not found: {} ({})", name, args)
            return super.invokeMethod(name, args)
        }
    }

    @Override
    Closure register(String name, Closure closure) {
        this.binding.closureMap[name] = closure
    }

    @Override
    Expando getContext() {
        return this.binding.context
    }

    private List<String> getViolations() {
        return getContext().violations
    }

}
