package de.qaware.qav.architecture.dsl.impl

import com.google.common.collect.Maps
import de.qaware.qav.architecture.dsl.model.Architecture
import groovy.util.logging.Slf4j

/**
 * This script class wraps the architecture script.
 *
 * @author QAware GmbH
 */
@Slf4j
abstract class ArchitectureDSL extends Script {

    Map<String, Architecture> architectures = Maps.newLinkedHashMap()

    /**
     * The content of the script will end up in this method
     */
    abstract void scriptBody()

    @Override
    Map<String, Architecture> run() {
        Long start = System.currentTimeMillis()
        scriptBody()
        Long duration = System.currentTimeMillis() - start
        log.debug("Done parsing, took ${duration}ms.")

        return architectures
    }

    /**
     * Implements the "architecture" keyword in the Architecture DSL.
     *
     * @param values the attributes passed to the architecture
     * @param closure the closure which defines the content of the architecture, i.e. the components etc.
     */
    void architecture(Map values, Closure closure) {
        Architecture architecture = new Architecture(values)
        architectures[architecture.name] = architecture
        log.debug("architecture ${architecture.name}")

        ArchitectureSpec architectureSpec = new ArchitectureSpec(null)
        architectureSpec.component = architecture
        def code = closure.rehydrate(architectureSpec, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()

        architecture.allComponents = architectureSpec.allComponents
    }
}
