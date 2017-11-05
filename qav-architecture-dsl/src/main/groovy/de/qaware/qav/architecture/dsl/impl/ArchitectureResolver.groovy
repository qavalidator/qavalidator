package de.qaware.qav.architecture.dsl.impl

import de.qaware.qav.architecture.dsl.model.Architecture
import de.qaware.qav.architecture.dsl.model.ClassSet
import de.qaware.qav.architecture.dsl.model.Component
import groovy.util.logging.Slf4j

/**
 * Resolve all "uses" references within an {@link Architecture}.
 *
 * @author QAware GmbH
 */
@Slf4j
class ArchitectureResolver {

    /**
     * Resolve all "uses" references within the {@link de.qaware.qav.architecture.dsl.model.Architecture}.
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
            cmp.uses.values().each { ClassSet classSet ->
                success &= checkUsesRelation(architecture, cmp, classSet)
            }
        }
        assert success: "Architecture ${architecture.name} contains errors."
    }

    private boolean checkUsesRelation(Architecture architecture, Component cmp, ClassSet classSet) {
        boolean success = true
        classSet.getPatterns().each { String target ->
            if (!architecture.apiNameToComponent[target]) {
                success = false
                log.error("Component or API ${target} missing in Component '${cmp.name}'.uses[${classSet.name}]")
            }
        }
        return success
    }
}
