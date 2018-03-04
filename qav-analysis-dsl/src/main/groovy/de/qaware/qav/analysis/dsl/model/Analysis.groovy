package de.qaware.qav.analysis.dsl.model

/**
 * @author QAware GmbH
 */
interface Analysis {

    /**
     * Registers the closure under the given name.
     *
     * @param name name of the closure.
     * @param closure closure to call.
     * @return the closure.
     */
    Closure register(String name, Closure closure)

    /**
     * Returns the <em>Context</em> instance of the current script.
     *
     * @return the context (an {@link Expando})
     */
    Expando getContext()

    /**
     * Report an error.
     *
     * @param msg the message
     */
    void error(String msg)

    /**
     * Report an error.
     *
     * @param throwable the cause
     */
    void error(Throwable throwable)
}