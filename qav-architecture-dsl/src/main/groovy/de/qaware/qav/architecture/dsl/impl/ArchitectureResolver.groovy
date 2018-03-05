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
     * @throws IllegalArgumentException if there are undefined references
     */
    static void resolveArchitecture(Architecture architecture) {
        setupIndexMaps(architecture)
        checkUsesRelations(architecture)
    }

    /**
     * Create the lookup maps to find components by their name, their API names, or their IMPL names.
     *
     * @param architecture the {@link Architecture}
     * @return the List of {@link Component}s
     */
    private static Component[] setupIndexMaps(Architecture architecture) {
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

    /**
     * Checks that all declared references have a valid target.
     *
     * @param architecture the {@link Architecture}
     */
    private static void checkUsesRelations(Architecture architecture) {
        boolean success = true
        architecture.allComponents.each { cmp ->
            cmp.usesAPI.values().each {ClassSet classSet ->
                success &= checkUsesRelation(architecture.apiNameToComponent, cmp, classSet)
            }
            cmp.usesImpl.values().each {ClassSet classSet ->
                success &= checkUsesRelation(architecture.implNameToComponent, cmp, classSet)
            }
        }
        if (!success) {
            throw new IllegalArgumentException("Architecture ${architecture.name} contains errors.")
        }
    }

    private static boolean checkUsesRelation(Map<String, Component> apiOrImplToComponentMap, Component cmp, ClassSet classSet) {
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
