package de.qaware.qav.app.server.mapper;

import de.qaware.qav.analysis.result.model.Result;
import de.qaware.qav.analysis.result.model.ResultType;
import de.qaware.qav.app.server.model.ImageDTO;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ImageMapper}
 */
public class ImageMapperTest {

    private static final String BASEDIR = "src/test/resources/analysis-result";

    @Test
    public void toDTO() {
        Result p1 = new Result(ResultType.IMAGE, "p1", 12, 24);
        Result p2 = new Result(ResultType.IMAGE, "p2", 12, 24);
        Result p3 = new Result(ResultType.TEXT, "p4");

        ImageDTO i1 = ImageMapper.toDTO(BASEDIR, p1);
        ImageDTO i2 = ImageMapper.toDTO(BASEDIR, p2);
        ImageDTO i3 = ImageMapper.toDTO(BASEDIR, p3);

        assertThat(i1).isNotNull();
        assertThat(i1.getFilenamePNG()).isEqualTo("p1.png");
        assertThat(i1.getFilenameSVG()).isEqualTo("p1.svg");
        assertThat(i1.getFilenameGraphml()).isEqualTo("p1.graphml");

        assertThat(i2).isNotNull();
        assertThat(i2.getFilenamePNG()).isEqualTo("p2.png");
        assertThat(i2.getFilenameSVG()).isNull();
        assertThat(i2.getFilenameGraphml()).isNull();

        assertThat(i3).isNotNull();
        assertThat(i3.getFilenamePNG()).isNull();
        assertThat(i3.getFilenameSVG()).isNull();
        assertThat(i3.getFilenameGraphml()).isNull();
    }
}