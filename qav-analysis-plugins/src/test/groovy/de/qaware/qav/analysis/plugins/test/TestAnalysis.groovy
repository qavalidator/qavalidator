package de.qaware.qav.analysis.plugins.test

import de.qaware.qav.analysis.dsl.model.Analysis
import groovy.util.logging.Slf4j

/**
 * Mock analysis object.
 *
 * @author QAware GmbH
 */
@Slf4j
class TestAnalysis implements Analysis {

    Map<String, Closure> closureMap = [:]
    final Expando context = new Expando()
    final errorMessages = []
    final calledMethods = []
    final calledMethodsArgs = [:]
    final violationMessages = []

    @Override
    Closure register(String name, Closure closure) {
        return closureMap.put(name, closure)
    }

    @Override
    Expando getContext() {
        return context
    }

    @Override
    void error(String msg) {
        log.error(msg)
        errorMessages << msg
    }

    @Override
    void error(Throwable throwable) {
        this.error(throwable.message)
    }

    @Override
    void violation(String msg) {
        this.violationMessages << msg
    }

    @Override
    Object invokeMethod(String name, Object args) {
        calledMethods << name
        calledMethodsArgs[name] = args
        log.warn("Called method on analysis: ${name} (args: ${args})")
        return null
    }
}
