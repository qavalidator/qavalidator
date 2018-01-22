package de.qaware.qav.visualization;

import de.qaware.qav.util.FileSystemUtil;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;


/**
 * @author QAware GmbH
 */
public class LegendCreatorTest {

    @Test
    public void export() {
        String filenameBase = "build/test-output/legend";
        new LegendCreator().export(filenameBase);

        String filename = filenameBase + ".dot";
        assertThat("Output missing: " + filename, FileSystemUtil.checkFileOrResourceExists(filename));
    }
}