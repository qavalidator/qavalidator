import de.qaware.qav.architecture.dsl.model.Architecture

apply "de.qaware.qav.analysis.plugins.ShortcutQavPlugin"

analysis("Step 1: Read Typescript file") {
    inputTypescript "../../qav-input/src/test/resources/typescript/ts-qav-export.xml"

    tsFileGraph = typescriptDependencyGraph.filter(nodePropertyInFilter("typescript.type", "File"))
    tsPackageGraph = createPackageArchitectureView(tsFileGraph, "TS-Package", "/")

    writeDot(tsFileGraph, "typescript", new Architecture())
    writeDot(tsPackageGraph, "typescript-architecture", architecture("TS-Package"))
}

analysis("Step 2: apply architecture") {
    readArchitecture "architecture-typescript.groovy"
    architectureTView = createArchitectureView(tsFileGraph, architecture("T-View"))

    writeDot(architectureTView, "typescript-t-view", architecture("T-View"))
}

analysis("Step 3: Write output") {
    writeFile(typescriptDependencyGraph, "typescript.json")
}