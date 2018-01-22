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

        assertThat("Output missing: " + filenameBase, FileSystemUtil.checkFileOrResourceExists(filenameBase + ".png"));
    }
}