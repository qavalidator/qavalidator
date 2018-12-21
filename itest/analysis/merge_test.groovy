apply "de.qaware.qav.analysis.plugins.ShortcutQavPlugin"

analysis("Read and merge files") {
    def graph = readFile baseDir : '.',
            includes : "**/dependencyGraph.json"

    writeFile graph, "merged-graph.json"
    def inputGraph = graph.filter(nodePropertyInFilter("scope", "input"))
    writeFile inputGraph, "input-graph.json"

    createPackageArchitectureView inputGraph
    def architectureGraph = graph.filter(nodePropertyInFilter("type", "architecture"))
    writeDot architectureGraph, "merged-architecture-graph", architecture("Package")
}
