package de.qaware.qav.analysis.plugins.base

import de.qaware.qav.analysis.dsl.model.QavPlugin
import de.qaware.qav.analysis.dsl.model.Analysis

/**
 * Base class with the commonalities of most {@link QavPlugin}s.
 *
 * @author QAware GmbH
 */
abstract class BasePlugin implements QavPlugin {

    protected Analysis analysis
    protected Expando context

    @Override
    void apply(Analysis analysis) {
        this.analysis = analysis
        this.context = analysis.context
    }

}
