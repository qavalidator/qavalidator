package de.qaware.qav.analysis.plugins

import de.qaware.qav.analysis.dsl.model.Analysis
import de.qaware.qav.analysis.plugins.analysis.AnalysisQavPlugin
import de.qaware.qav.analysis.plugins.analysis.ArchitectureQavPlugin
import de.qaware.qav.analysis.plugins.analysis.GraphFilterQavPlugin
import de.qaware.qav.analysis.plugins.base.BasePlugin
import de.qaware.qav.analysis.plugins.input.JavaQavPlugin
import de.qaware.qav.analysis.plugins.input.MavenQavPlugin
import de.qaware.qav.analysis.plugins.input.TracesQavPlugin
import de.qaware.qav.analysis.plugins.input.TypescriptQavPlugin
import de.qaware.qav.analysis.plugins.output.IOQavPlugin
import de.qaware.qav.analysis.plugins.output.SonarMessagesQavPlugin
import de.qaware.qav.doc.QavPluginDoc
import groovy.util.logging.Slf4j

/**
 * QAvalidator language shortcuts for standard usage.
 *
 * @author QAware GmbH
 */
@QavPluginDoc(name = "ShortcutQavPlugin",
        description = """
                The plugin `ShortcutQavPlugin` combines the most common preparations, and registers all of the above listed plugins.

                * It registers other plugins (`apply`)
                * It define filters: ignoreUtils, classes, modules, inputScope, mavenScope, cycleFilter
                * It defines filtered Graphs:
                  ** allClassesGraph: all nodes in `context.dependencyGraph` which represent a class, i.e. it filters all nodes:
                     *** Property "type" has value "class": it filters out all nodes which represent "component" instead of "class".
                     *** Property "name" not in `"java.lang.xxx"`, `"java.util.xxx"`, `"java.io.xxx"`, `"org.slf4j.xxx"`: No util classes.
                  ** inputClassesGraph: all of `allClassesGraph`, but also:
                     *** Property "scope" has value "input": Only the classes in the input scope, filter out all referenced classes
                         which are not part of the input scope
                """)
@Slf4j
class ShortcutQavPlugin extends BasePlugin {

    @Override
    void apply(Analysis analysis) {
        super.apply(analysis)

        // Apply required plugins
        // analysis
        new AnalysisQavPlugin().apply(analysis)
        new ArchitectureQavPlugin().apply(analysis)
        new GraphFilterQavPlugin().apply(analysis)

        // input
        new JavaQavPlugin().apply(analysis)
        new MavenQavPlugin().apply(analysis)
        new TracesQavPlugin().apply(analysis)
        new TypescriptQavPlugin().apply(analysis)

        // output
        new IOQavPlugin().apply(analysis)
        new SonarMessagesQavPlugin().apply(analysis)

        //Register commands
        analysis.analysis("00_initialize", this.&initializeAnalysis)
    }

    /**
     * Performs the initialization required to run standard analysis steps. Also, checks the input graph for cycles.
     * Creates: allClassesGraph, inputClassesGraph, classesCycleGraph
     */
    void initializeAnalysis() {
        analysis.addFilter("ignoreJavaLang", analysis.nodeNameOutFilter("java.lang.**"))
        analysis.addFilter("classes", analysis.nodePropertyInFilter("type", "class"))
        analysis.addFilter("modules", analysis.nodePropertyInFilter("type", "module"))
        analysis.addFilter("inputScope", analysis.nodePropertyInFilter("scope", "input"))
        analysis.addFilter("mavenScope", analysis.nodePropertyInFilter("scope", "maven"))
        analysis.addFilter("cycleFilter", analysis.nodePropertyInFilter(context.IN_CYCLE, true))

        context.allClassesGraph = context.dependencyGraph.filter(analysis.and(analysis.filter("classes"),
                analysis.filter("ignoreJavaLang")))
        context.inputClassesGraph = context.allClassesGraph.filter(analysis.filter("inputScope"))
    }
}
