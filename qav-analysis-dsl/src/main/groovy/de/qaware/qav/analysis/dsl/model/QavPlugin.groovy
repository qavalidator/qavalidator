package de.qaware.qav.analysis.dsl.model

/**
 * @author QAware GmbH
 */
interface QavPlugin {

    /**
     * Applies its extensions to the given {@link Analysis} object.
     *
     * @param analysis The analysis object.
     */
    void apply(Analysis analysis)

}