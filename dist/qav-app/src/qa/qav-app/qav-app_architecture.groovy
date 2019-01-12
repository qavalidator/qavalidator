/* ----------------------------------------------------------------------------------------------------
 * QAV Architecture Definition
 * ---------------------------------------------------------------------------------------------------- */

architecture(name: "T-View", prefix: "tview", reflexMLversion: "1.0") {

    includes "de.qaware.qav.**"
    excludes "de.qaware.qav.doc.**"

    component("Main") {
        api "de.qaware.qav.app.QavMain", "de.qaware.qav.app.QavApp"
    }

    component("QAV_Runner") {
        api "de.qaware.qav.runner.**"
    }

    component("AnalysisDSL") {
        api "de.qaware.qav.analysis.dsl.api.**"
        api "de.qaware.qav.analysis.dsl.model.**"
        impl "de.qaware.qav.analysis.dsl.impl.**"

        uses "Graph.factory"
    }

    component("AnalysisPlugins") {
        api "de.qaware.qav.analysis.plugins.**"

        uses "Graph.base", "Graph.alg", "Graph.filter", "Graph.factory", "Graph.io",
                "JavaInput", "MavenInput", "TracesInput", "TypescriptInput"
        usesImpl "JavaInput" // the Java plugin instantiates the JavaScopeReader implementation
    }

    component("ArchitectureDSL") {
        api "de.qaware.qav.architecture.dsl.api.**"
        api "de.qaware.qav.architecture.dsl.model.**"
        impl "de.qaware.qav.architecture.dsl.impl.**"
    }

    component("Architecture") {
        api "de.qaware.qav.architecture.checker.**"
        api "de.qaware.qav.architecture.factory.**"
        api "de.qaware.qav.architecture.viewcreator.**"
        api "de.qaware.qav.architecture.tagger.**"

        uses "Graph.base", "Graph.filter"
    }

    component("Graph") {
        component("Graph.base") {
            api "de.qaware.qav.graph.api.**"
            impl "de.qaware.qav.graph.impl.**"
        }

        component("Graph.alg") {
            api "de.qaware.qav.graph.alg.**"
            usesImpl "Graph.base" // Exception from the rule: some algorithms expect a specific graph implementation to work
        }
        component("Graph.factory") {
            api "de.qaware.qav.graph.factory.**"
            usesImpl "Graph.base" // Factory pattern: the factory has to instantiate a specific implementation class
        }
        component("Graph.filter") {api "de.qaware.qav.graph.filter.**"}
        component("Graph.io") {api "de.qaware.qav.graph.io.**"}
        component("Graph.index") {api "de.qaware.qav.graph.index.**"}
    }

    component("Input") {
        component("JavaInput") {
            api "de.qaware.qav.input.javacode.api.**"
            impl "de.qaware.qav.input.javacode.impl.**"
        }
        component("MavenInput") {api "de.qaware.qav.input.maven.**"}
        component("TypescriptInput") {api "de.qaware.qav.input.typescript.**"}
        component("TracesInput") {api "de.qaware.qav.input.traces.**"}

        uses "Graph.base"
    }

    component("App") {
        uses "Graph.base", "Graph.io", "Graph.index"

        component("Server") { api "de.qaware.qav.app.server.**" }
        component("CmdLine") { api "de.qaware.qav.app.cmdline.**" }
        component("Main") { api "de.qaware.qav.app.QavMain" }
    }

    component("Util") {
        api "de.qaware.qav.util.**"
    }

    component("Visualization") {
        api "de.qaware.qav.visualization.**"
        uses "Graph.filter", "Graph.base", "Graph.factory"
    }

}
