package de.qaware.qav.architecture.dsl.impl

import de.qaware.qav.architecture.dsl.model.Architecture
import de.qaware.qav.architecture.dsl.model.ClassSet
import de.qaware.qav.architecture.dsl.model.Component
import groovy.util.logging.Slf4j
import org.codehaus.groovy.control.CompilerConfiguration
import org.junit.Test

/**
 *
 * @author QAware GmbH
 */
@Slf4j
class ArchitectureDSLTest {

    @Test
    void testSimpleDSL() {
        def config = new CompilerConfiguration()
        config.scriptBaseClass = ArchitectureDSL.name
        def shell = new GroovyShell(config)
        Map<String, Architecture> architectures = shell.evaluate """
            architecture(name: "Test-1", prefix: "t1", reflexMLversion: "1.0") {
                includes "my.**"
                excludes "my.A.nested.impl.ignorable.**"
                
                api "my.api.**"
                impl "my.impl.**"
                impl "my.util.**"
                impl "test-only": "my.test.util.**",
                     "other-test": "my.other.test.**"
                uses "3rd-Party"

                component ("A") {
                    api "my.A.api.**"
                    impl "my.A.impl.**"
                    uses "B"

                    component ("A-nested") {
                        api "my.A.nested.api.**"
                        impl"my.A.nested.impl.**"
                        uses "3rd-Party"
                    }
                }

                component ("B") {
                    api "my.B.api.**"
                    impl "my.B.impl.**"
                }
            }
        """

        assert architectures != null
        Architecture architecture = architectures["Test-1"]
        assert architecture != null
        assert architecture.name == "Test-1"
        assert architecture.prefix == "t1"
        assert architecture.reflexMLversion == "1.0"
        assert architecture.api["Test-1"] == new ClassSet("Test-1", ["my.api.**"])
        assert architecture.impl["Test-1"].patterns == [ "my.impl.**", "my.util.**" ]
        assert architecture.impl["test-only"].patterns == [ "my.test.util.**"]
        assert architecture.impl["other-test"].patterns == [ "my.other.test.**"]
        assert architecture.uses["Test-1"].patterns == [ "3rd-Party" ]
        assert architecture.allComponents.size() == 4

        Component[] children = architecture.children
        assert children.size() == 2
        Component a = children[0]
        assert a.name == "A"
        assert a.api["A"].patterns == [ "my.A.api.**" ]
        Component b = children[1]
        assert b.name == "B"
        assert b.api["B"].patterns == [ "my.B.api.**" ]

        Component[] grandChildren = a.children
        assert grandChildren.size() == 1
        Component grandChild = grandChildren[0]
        assert grandChild.name == "A-nested"
        assert grandChild.api["A-nested"].patterns == [ "my.A.nested.api.**" ]

        assert architecture.isIncluded("my.A.api.Clazz")
        assert architecture.isIncluded("my.C.api.OtherClazz")
        assert !architecture.isIncluded("org.apache.api.SomeClazz")

        assert architecture.getParentComponent("my.api.Asdf").name == "Test-1"
        assert architecture.getParentComponent("my.A.impl.Asdf").name == "A"
        assert architecture.getParentComponent("my.A.nested.api.Asdf").name == "A-nested"

        assert architecture.getParentComponent("my/api/Asdf") == null
    }


    @Test
    void testDSLWithOtherPathSeparator() {
        def config = new CompilerConfiguration()
        config.scriptBaseClass = ArchitectureDSL.name
        def shell = new GroovyShell(config)
        Map<String, Architecture> architectures = shell.evaluate """
            architecture(name: "Test-1", prefix: "t1", reflexMLversion: "1.0") {
                includes "my/**"
                excludes "my/A/nested/impl/ignorable/**"
                pathSeparator "/"
                
                api "my/api/**"
                impl "my/impl/**"
                impl "my/util/**"
                impl "test-only": "my/test/util/**",
                     "other-test": "my/other/test/**"
                uses "3rd-Party"

                component ("A") {
                    api "my/A/api/**"
                    impl "my/A/impl/**"
                    uses "B"

                    component ("A-nested") {
                        api "my/A/nested/api/**"
                        impl"my/A/nested/impl/**"
                        uses "3rd-Party"
                    }
                }

                component ("B") {
                    api "my/B/api/**"
                    impl "my/B/impl/**"
                }
            }
        """

        assert architectures != null
        Architecture architecture = architectures["Test-1"]
        assert architecture != null
        assert architecture.name == "Test-1"
        assert architecture.prefix == "t1"
        assert architecture.reflexMLversion == "1.0"
        assert architecture.api["Test-1"] == new ClassSet("Test-1", ["my/api/**"])
        assert architecture.impl["Test-1"].patterns == [ "my/impl/**", "my/util/**" ]
        assert architecture.impl["test-only"].patterns == [ "my/test/util/**"]
        assert architecture.impl["other-test"].patterns == [ "my/other/test/**"]
        assert architecture.uses["Test-1"].patterns == [ "3rd-Party" ]
        assert architecture.allComponents.size() == 4

        Component[] children = architecture.children
        assert children.size() == 2
        Component a = children[0]
        assert a.name == "A"
        assert a.api["A"].patterns == [ "my/A/api/**" ]
        Component b = children[1]
        assert b.name == "B"
        assert b.api["B"].patterns == [ "my/B/api/**" ]

        Component[] grandChildren = a.children
        assert grandChildren.size() == 1
        Component grandChild = grandChildren[0]
        assert grandChild.name == "A-nested"
        assert grandChild.api["A-nested"].patterns == [ "my/A/nested/api/**" ]

        assert architecture.isIncluded("my/A/api/Clazz")
        assert architecture.isIncluded("my/C/api/OtherClazz")
        assert !architecture.isIncluded("org/apache/api/SomeClazz")

        assert architecture.getParentComponent("my/api/Asdf").name == "Test-1"
        assert architecture.getParentComponent("my/A/impl/Asdf").name == "A"
        assert architecture.getParentComponent("my/A/nested/api/Asdf").name == "A-nested"

        assert architecture.getParentComponent("my.api.Asdf") == null
    }
}
