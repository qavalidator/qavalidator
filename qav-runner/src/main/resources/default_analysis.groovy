apply "de.qaware.qav.analysis.plugins.ShortcutQavPlugin"

analysis("Step 1: Analyze Package Architecture") {
    // Prepare input: analyze the graph and create an architecture tree, based on the package hierarchy:
    // packageGraph is now a filtered graph which only contains those nodes which belong to the package architecture
    // (i.e. it does not contain the class nodes).
    def packageGraph = createPackageArchitectureView(inputClassesGraph)

    def packageCycleGraph = findCycles(packageGraph, "Package")

    // output:
    printNodes(packageCycleGraph, "packageCycleNodes.txt")
    writeDot(packageGraph, "packageGraph", architecture("Package"))
    writeFile(dependencyGraph, "dependencyGraph.json")
}
