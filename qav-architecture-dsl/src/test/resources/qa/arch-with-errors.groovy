package qa
/* ----------------------------------------------------------------------------------------------------
 * QAV Architecture Definition
 * ---------------------------------------------------------------------------------------------------- */

architecture(name: "Test-1", prefix: "t1", reflexMLversion: "1.0") {
    api "my.api"
    impl "my.impl"
    impl "my.util"
    uses "3rd-Party" // error: not defined

    component ("A") {
        api "my.A.api"
        impl "my.A.impl"
        uses "B"

        component ("A-nested") {
            api "my.A.nested.api"
            impl "my.A.nested.impl"
            uses "3rd-Party"
        }
    }

    component ("B") {
        api "my.B.api"
        impl "my.B.impl"
    }
}
