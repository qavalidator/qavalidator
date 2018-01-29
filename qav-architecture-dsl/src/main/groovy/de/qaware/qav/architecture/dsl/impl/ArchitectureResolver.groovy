package de.qaware.qav.architecture.dsl.impl

import de.qaware.qav.architecture.dsl.model.Architecture
import de.qaware.qav.architecture.dsl.model.ClassSet
import de.qaware.qav.architecture.dsl.model.Component
import groovy.util.logging.Slf4j

/**
 * Resolve all "usesAPI" references within an {@link Architecture}.
 *
 * @author QAware GmbH
 */
@Slf4j
class ArchitectureResolver {

    /**
     * Resolve all "usesAPI" references within the {@link de.qaware.qav.architecture.dsl.model.Architecture}.
     *
     * @param architecture the {@link de.qaware.qav.architecture.dsl.model.Architecture} to check
     * @throws AssertionError if there are undefined references
     */
    void resolveArchitecture(Architecture architecture) {
        setupIndexMaps(architecture)
        checkUsesRelations(architecture)
    }

    private Component[] setupIndexMaps(Architecture architecture) {
        architecture.allComponents.each { cmp ->
            architecture.nameToComponent[cmp.name] = cmp

            cmp.api.each { name, set ->
                architecture.apiNameToComponent[name] = cmp
            }
            cmp.impl.each { name, set ->
                architecture.implNameToComponent[name] = cmp
            }
        }
    }

    private void checkUsesRelations(Architecture architecture) {
        boolean success = true
        architecture.allComponents.each { cmp ->
            cmp.usesAPI.values().each {ClassSet classSet ->
                success &= checkUsesRelation(architecture.apiNameToComponent, cmp, classSet)
            }
            cmp.usesImpl.values().each {ClassSet classSet ->
                success &= checkUsesRelation(architecture.implNameToComponent, cmp, classSet)
            }
        }
        assert success: "Architecture ${architecture.name} contains errors."
    }

    private boolean checkUsesRelation(Map<String, Component> apiOrImplToComponentMap, Component cmp, ClassSet classSet) {
        boolean success = true
        classSet.getPatterns().each { String target ->
            if (!apiOrImplToComponentMap[target]) {
                success = false
                log.error("API or Impl ${target} missing in Component '${cmp.name}'")
            }
        }
        return success
    }
}
