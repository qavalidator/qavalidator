package qa
/* ----------------------------------------------------------------------------------------------------
 * QAV Architecture Definition
 * ---------------------------------------------------------------------------------------------------- */

architecture(name: "Test-1", prefix: "t1", reflexMLversion: "1.0") {
    api "com.my.api.*"
    impl "com.my.impl.*"
    impl "com.my.util.*"
    uses "3rd-Party"

    component ("A") {
        api "com.my.a.api.*"
        impl "com.my.a.impl.*"
        uses "B"

        component ("A-nested") {
            api "com.my.a.d.api.*"
            impl "com.my.a.d.impl.*"
        }
    }

    component ("B") {
        api "com.my.bb.api.*"
        impl "com.my.bb.impl.*"
    }

    component ("C") {
        api "com.my.cc.api.*"
        impl "com.my.cc.impl.*"
    }

    component ("3rd-Party") {
        api "com.google.commons.*", "org.apache.commons.*"
    }
}
