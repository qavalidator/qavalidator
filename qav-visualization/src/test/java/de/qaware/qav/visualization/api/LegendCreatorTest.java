package de.qaware.qav.visualization.api;

import de.qaware.qav.util.FileSystemUtil;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author QAware GmbH
 */
public class LegendCreatorTest {

    @Test
    public void export() {
        String filenameBase = "build/test-output/legend";
        new LegendCreator().export(filenameBase);

        String filename = filenameBase + ".dot";
        assertThat(FileSystemUtil.checkFileOrResourceExists(filename)).isTrue();
    }
}