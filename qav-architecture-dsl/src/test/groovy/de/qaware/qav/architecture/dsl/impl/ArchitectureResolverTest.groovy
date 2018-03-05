package de.qaware.qav.architecture.dsl.impl

import de.qaware.qav.architecture.dsl.model.Architecture
import org.codehaus.groovy.control.CompilerConfiguration
import org.junit.Test

/**
 *
 * @author QAware GmbH
 */
class ArchitectureResolverTest {

    @Test
    void testResolveArchitecture() {
        Architecture architecture = createArchitecture()

        ArchitectureResolver.resolveArchitecture(architecture)
        assert architecture.nameToComponent.size() == 4
    }

    private Architecture createArchitecture() {
        def config = new CompilerConfiguration()
        config.scriptBaseClass = ArchitectureDSL.name
        def shell = new GroovyShell(config)
        Map<String, Architecture> architectures = shell.evaluate """
            architecture(name: "Test-1", prefix: "t1", reflexMLversion: "1.0") {
                api "my.api"
                impl "my.impl"
                impl "my.util"
                uses "3rd-Party"
                api "3rd-Party": ["java.util.*"]

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
        """

        Architecture architecture = architectures["Test-1"]
        assert architecture
        architecture
    }
}
