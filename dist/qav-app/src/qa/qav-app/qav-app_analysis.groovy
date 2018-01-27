apply "de.qaware.qav.analysis.plugins.ShortcutQavPlugin"

/* ----------------------------------------------------------------------------------------------------
 * Analysis
 * ---------------------------------------------------------------------------------------------------- */

systemName = "qav-app"

analysis("Step 0: Initialize") {
    // this example shows that we're analyzing a JAR file which has nested JARs in it:
    inputJava baseDir: "build/libs",
            includes: ["**/de/qaware/qav/**/*.class", "**/qav-*.jar"]

    outputDir "build/results-${systemName}"
}

analysis("Step 1: Analyze Package Architecture") {
    // Prepare input: analyze the graph and create an architecture tree, based on the package hierarchy:
    // packageGraph is now a filtered graph which only contains those nodes which belong to the package architecture
    // (i.e. it does not contain the class nodes).
    def packageGraph = createPackageArchitectureView(inputClassesGraph)

    // Find cycles on package level.
    def packageCycleGraph = findCycles(packageGraph, "Package")

    // output:
    printNodes(packageCycleGraph, "packageCycleNodes.txt")
    writeDot(packageGraph, "packageGraph", architecture("Package"))
}

analysis("Step 2: Analyze T-View Architecture") {
    // Read the given Architecture DSL file. The architecture will be available under the name defined in the
    // Architecture DSL file; it can be accessed with: architecture("T-View")
    readArchitecture "qav-app_architecture.groovy"

    // Use that architecture and apply it on the dependency graph.
    architectureTView = createArchitectureView(allClassesGraph, architecture("T-View"))

    // Check all architecture rules: all relations must be covered in the architecture definition, all components must
    // actually be implemented, and all rules in the architecture file are really used.
    checkArchitectureRules(architectureTView, architecture("T-View"))
}

analysis("Step 3: Export as DOT, GraphML, and JSON") {
    // graphical export as DOT (for GraphViz) and GraphML (for yEd)
    writeDot(architectureTView, "architectureTView", architecture("T-View"))
    writeGraphLegend()

    // this is to import it into qav-server for interactive exploration of the dependency graph
    writeFile(dependencyGraph, "dependencyGraph.json")
}
