

architecture(name: "T-View", prefix: "tview", reflexMLversion: "1.0") {

    pathSeparator "/"

    component("data") {
        api "src/dataModel/**"
    }

    component("export") { api "src/export/**" }
    component("parser") { api "src/parser/**" }
    component("linker") { api "src/linker/**" }
    component("tools") { api "src/tools/**" }
    component("utility") { api "src/utility/**" }

    component("ts-base") { api "typescript" }

    component("rest") {
        api "**"
    }
}
