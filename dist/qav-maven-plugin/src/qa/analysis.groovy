apply "de.qaware.qav.core.analysis.dsl.plugins.ShortcutQavPlugin"

analysis("Step 1: Analyze Package Architecture") {
    createPackageArchitectureView allClassesGraph

    def packageGraph = createArchitectureView(allClassesGraph, architecture("Package"))
    def packageGraphOnInput = createArchitectureView(inputClassesGraph, architecture("Package"), "packageOnInput")

    def packageCycleGraph = findCycles(packageGraphOnInput, "Package")
    printNodes(packageCycleGraph, "packageCycleNodes.txt")

    // write DOT only in the end in order to include the red "cycle" markers
    writeDot(packageGraph, "packageGraph", architecture("Package"))
    writeDot(packageGraphOnInput, "packageGraphOnInput", architecture("Package"))

    writeFile(dependencyGraph, "dependencyGraph.json")
    writeFile(packageGraph, "packageGraph.json")
    writeFile(packageGraphOnInput, "packageGraphOnInput.json")
}
