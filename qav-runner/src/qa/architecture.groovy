/* ----------------------------------------------------------------------------------------------------
 * QAV Architecture Definition
 * ---------------------------------------------------------------------------------------------------- */

architecture(name: "T-View", prefix: "tview", reflexMLversion: "1.0") {

    excludes "java.util.**", "java.io.**", "org.slf4j.**"

    uses "Apache Commons", "Guava", "Groovy"

    component("QAV_Runner") {
        api "de.qaware.qav.runner.**"
    }

    component("AnalysisDSL") {
        api "de.qaware.qav.analysis.dsl.api.**"
        api "de.qaware.qav.analysis.dsl.model.**"
        impl "de.qaware.qav.analysis.dsl.impl.**"
    }

    component("Util") {
        api "de.qaware.qav.util.**"
    }

    component("3rdParty") {
        component("Groovy") {api "org.codehaus.groovy.*", "groovy.lang.*", "groovy.util.*"}
        component("Apache Commons") {api "org.apache.commons.*"}
        component("Guava") {api "com.google.common.*"}
    }
}
