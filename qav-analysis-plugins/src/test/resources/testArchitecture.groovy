/*
 * test architecture
 */
architecture(name: "T-View", prefix: "tview", reflexMLversion: "1.0") {

    component("V") {api "v*"}
    component("3rdParty") {
        component("Unwanted") {api "unwanted.*"}
    }

}
