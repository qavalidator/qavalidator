package de.qaware.qav.architecture.dsl.api

import de.qaware.qav.architecture.dsl.model.Architecture
import org.junit.Test

/**
 * Tests for {@link QavArchitectureReader}.
 *
 * @author QAware GmbH
 */
class QavArchitectureReaderTest {

    @Test
    void testRead() {
        QavArchitectureReader reader = new QavArchitectureReader("src/test/resources/qa/architecture.groovy", null)
        assert reader
        reader.read()

        Architecture architecture = reader.getArchitectures()["T-View"]
        assert architecture != null

        assert architecture.getChildren().size() == 4
    }

    @Test
    void testReadWithErrors() {
        QavArchitectureReader reader = new QavArchitectureReader("classpath:/qa/arch-with-errors.groovy", null)

        try {
            reader.read()
        } catch(AssertionError e) {
            assert e.message == "Architecture Test-1 contains errors.. Expression: success. Values: success = false"
        }
    }

    @Test
    void testReadNotExisting() {
        try {
            new QavArchitectureReader("/not/existing", null)
        } catch (IllegalArgumentException e) {
            assert e.getMessage() =~ /File '.+not.existing' not found/
        }
    }
}
