/* ----------------------------------------------------------------------------------------------------
 * QAV Architecture Definition
 * ---------------------------------------------------------------------------------------------------- */

architecture(name: "T-View", prefix: "tview", reflexMLversion: "1.0") {

    excludes "java.util.**", "java.io.**", "javax.**", "org.slf4j.**", "lombok.**"

    component("QAV_Runner") {
        api "de.qaware.qav.runner.**"
    }

    component("QAV_App") {
        api "de.qaware.qav.app.**"
        uses "Spring", "Guava", "Analysis.Result"
    }

    component("Graph") { api "de.qaware.qav.graph.**" }

    component("Analysis") {
        component("Analysis.Result") {
            api "de.qaware.qav.analysis.result.**"
        }
    }

    component("Util") {
        api "de.qaware.qav.util.**"
    }

    component("3rdParty") {
        component("Spring") { api "org.springframework.**" }
        component("Guava") {api "com.google.common.**"}
    }
}
