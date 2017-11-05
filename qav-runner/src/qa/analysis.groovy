/*
 * QAvalidator Analysis Definition
 */

apply "de.qaware.qav.analysis.plugins.ShortcutQavPlugin"

analysis ("Step 0: Preparations") {
    outputDir "build/result-self", true
    abbreviation "de.qaware.qav.core", "Q"
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

    // do the same again, but limit the depth of the package hierarchy to 5:
    def packageGraph5 = createPackageArchitectureView(inputClassesGraph, 5)
    def packageCycleGraph5 = findCycles(packageGraph5, "Package-5")
    printNodes(packageCycleGraph5, "packageCycleNodes5.txt")
    writeDot(packageGraph5, "packageGraph5", architecture("Package-5"))
}

analysis("Step 2: Create and analyze T-View architecture graph") {
    // Read the given Architecture DSL file. The architecture will be available under the name defined in the
    // Architecture DSL file; it can be accessed with: architecture("T-View")
    readArchitecture "architecture.groovy"

    // Use that architecture and apply it on the dependency graph.
    // Do so twice: once for the full graph, and once only on the part of graph which represents only classes from
    // the input scope (leaving out referenced 3rd-party classes).
    architectureTView = createArchitectureView(allClassesGraph, architecture("T-View"))
    architectureTViewOnInput = createArchitectureView(inputClassesGraph, architecture("T-View"), "T-View-on-Input")
}

analysis("Step 3: Analyze and check for violations") {
    // Check all architecture rules: all relations must be covered in the architecture definition, all components must
    // actually be implemented, and all rules in the architecture file are really used.
    checkArchitectureRules(architectureTView, architecture("T-View"))

    // Find cycles on the component level
    architectureTViewCycleGraph = findCycles(architectureTView, "T-View")
}

analysis("Step 4: Export as DOT, GraphML, and JSON") {
    // graphical export as DOT (for GraphViz) and GraphML (for yEd)
    writeDot(architectureTView, "architectureTView", architecture("T-View"))
    writeDot(architectureTView.filter(nodeNameOutFilter("Apache Commons", "Guava", "Groovy", "3rdParty")), "architectureTView.short", architecture("T-View"))
    writeDot(architectureTViewCycleGraph, "architectureTViewCycleGraph", architecture("T-View"))
    writeDot(architectureTViewOnInput, "architectureTViewOnInput", architecture("T-View"))

    // this is to import it into qav-server for interactive exploration of the dependency graph
    writeFile(dependencyGraph, "dependencyGraph.json")
}
