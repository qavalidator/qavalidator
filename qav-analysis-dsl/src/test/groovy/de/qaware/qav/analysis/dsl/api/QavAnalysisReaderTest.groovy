package de.qaware.qav.analysis.dsl.api

import org.junit.Test

/**
 * Test for {@link QavAnalysisReader}.
 * 
 * @author QAware GmbH
 */
class QavAnalysisReaderTest {

    @Test(expected = IllegalArgumentException)
    void testWithNotExistingFile() {
        new QavAnalysisReader("not/existing/file")
    }

    @Test(expected = IllegalArgumentException)
    void testWithClasspathUrlNotExisting() {
        new QavAnalysisReader("classpath:/not/existing/resource")
    }

    @Test(expected = IllegalArgumentException)
    void testWithClasspathEmptyFile() {
        new QavAnalysisReader("classpath:/empty_architecture.groovy")
    }

}
