package de.qaware.qav.architecture.dsl.impl

import de.qaware.qav.architecture.dsl.model.ClassSet
import de.qaware.qav.architecture.dsl.model.Component
import groovy.util.logging.Slf4j

/**
 * A Component definition, created from the QAvalidator Architecture DSL.
 *
 * All public methods represent "keywords" defined in the QAvalidator Architecture DSL.
 *
 * @author QAware GmbH
 */
@Slf4j
class ComponentSpec {

    protected Component component
    protected String name

    List<Component> allComponents = []

    ComponentSpec(Component parent) {
        this.component = new Component()
        this.component.parent = parent
        this.allComponents << component
    }

    void setComponent(Component component) {
        this.component = component
        name = this.component.name
        this.allComponents = [component]
    }

    void setName(String name) {
        log.debug("component name: ${name}")
        this.component.name = name
        this.name = name
    }

    /*
     * keyword "pathSeparator"
     */

    void pathSeparator(String pathSeparator) {
        log.debug("pathSeparator: ${pathSeparator}")
        component.pathSeparator = pathSeparator
    }

    /*
     * keyword "api"
     */

    void api(String... patterns) {
        log.debug("component api: ${patterns}")
        ClassSet set = getClassSet(component.api, name)
        set.addPatterns(patterns)
    }

    void api(Map<String, String[]> values) {
        log.debug("api definition: ${values}")
        values.each {apiName, patterns ->
            ClassSet set = getClassSet(component.api, apiName)
            set.addPatterns(patterns)
        }
    }

    /*
     * keyword "impl"
     */

    void impl(String... patterns) {
        log.debug("component impl: ${patterns}")
        ClassSet set = getClassSet(component.impl, name)
        set.addPatterns(patterns)
    }

    void impl(Map<String, String[]> values) {
        log.debug("impl definition: ${values}")
        values.each {implName, patterns ->
            ClassSet set = getClassSet(component.impl, implName)
            set.addPatterns(patterns)
        }
    }

    /*
     * keyword "uses"
     */

    void uses(String... patterns) {
        log.debug("component uses: ${patterns}")
        ClassSet set = getClassSet(component.usesAPI, name)
        set.addPatterns(patterns)
    }

    void uses(Map<String, String[]> values) {
        log.debug("uses definition: ${values}")
        values.each {usesName, patterns ->
            ClassSet set = getClassSet(component.usesAPI, usesName)
            set.addPatterns(patterns)
        }
    }

    /*
     * keyword "usesImpl"
     */

    void usesImpl(String... patterns) {
        log.debug("component usesImpl: ${patterns}")
        ClassSet set = getClassSet(component.usesImpl, name)
        set.addPatterns(patterns)
    }

    void usesImpl(Map<String, String[]> values) {
        log.debug("usesImpl definition: ${values}")
        values.each {usesImplName, patterns ->
            ClassSet set = getClassSet(component.usesImpl, usesImplName)
            set.addPatterns(patterns)
        }
    }

    /**
     * This allows for nested components.
     *
     * @param name the name of the component
     * @param cl the closure which defines the nested component
     */
    void component(String name, Closure cl) {
        ComponentSpec componentSpec = new ComponentSpec(this.component)
        componentSpec.setName(name)
        def code = cl.rehydrate(componentSpec, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()

        this.component.children << componentSpec.component
        this.allComponents.addAll componentSpec.allComponents
    }

    /**
     * get a {@link ClassSet} from the given map. Creates a new one if it does not exist.
     *
     * @param map the map
     * @param name the name
     * @return the {@link ClassSet}
     */
    protected ClassSet getClassSet(Map<String, ClassSet> map, String name) {
        ClassSet set = map.get(name)
        if (set == null) {
            set = new ClassSet(name)
            map.put(name, set)
            String pathSeparator = component.pathSeparator
            if (pathSeparator) {
                set.pathSeparator = pathSeparator
            }
        }
        return set
    }
}
