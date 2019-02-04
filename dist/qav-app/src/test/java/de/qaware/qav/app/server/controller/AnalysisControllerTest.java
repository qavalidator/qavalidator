package de.qaware.qav.app.server.controller;

import de.qaware.qav.analysis.result.model.AnalysisResult;
import de.qaware.qav.app.server.exceptions.NotFoundException;
import de.qaware.qav.app.server.model.ImageDTO;
import org.junit.Test;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * Tests for {@link AnalysisController}
 */
public class AnalysisControllerTest {

    public static final String BASEDIR = "src/test/resources/analysis-result/";
    public static final String BASEDIR_2 = "src/test/resources/analysis-result-2/";
    public static final String BASEDIR_3 = "src/test/resources/analysis-result-3/";

    @Test
    public void testWithFilename() {
        AnalysisController ac = new AnalysisController();
        ac.setFilename(BASEDIR + "qav-analysis-result.json");
        ac.setGraphFilename("");

        ac.init();

        AnalysisResult analysisResult = ac.getAnalysisResult();

        assertThat(analysisResult).isNotNull();
        assertThat(analysisResult.getAnalysisId()).isEqualTo("qav-d3df7259-f41b-4139-bebc-46be03f85a0a");
    }

    @Test
    public void testWitWrongFilename() {
        AnalysisController ac = new AnalysisController();
        ac.setFilename("not/existing/qav-analysis-result.json");

        try {
            ac.init();
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).startsWith("File not found: ");
        }
    }

    @Test
    public void testWithGraphFilename() {
        AnalysisController ac = new AnalysisController();
        ac.setFilename("");
        ac.setGraphFilename(BASEDIR_2 + "dependencyGraph.json");

        ac.init();

        AnalysisResult analysisResult = ac.getAnalysisResult();

        assertThat(analysisResult).isNotNull();
        assertThat(analysisResult.getAnalysisId()).isEqualTo("qav-result-2");
    }

    @Test
    public void testWithFallbackNotWorking() {
        AnalysisController ac = new AnalysisController();
        ac.setFilename("");
        ac.setGraphFilename(BASEDIR_3 + "dependencyGraph.json");

        ac.init();
        assertNotFound(ac);
    }

    @Test
    public void testWithGraphFileWithoutParent() {
        AnalysisController ac = new AnalysisController();
        ac.setFilename("");
        ac.setGraphFilename("dependencyGraph.json");

        ac.init();
        assertNotFound(ac);
    }

    @Test
    public void testWithoutFilename() {
        AnalysisController ac = new AnalysisController();
        ac.setFilename("");
        ac.setGraphFilename("");

        ac.init();
        assertNotFound(ac);
    }

    private void assertNotFound(AnalysisController ac) {
        try {
            ac.getAnalysisResult();
            fail("NotFoundException expected");
        } catch (NotFoundException e) {
            assertThat(e.getMessage()).isEqualTo("Analysis file not found. No results being served.");
        }
    }

    @Test
    public void testGetImages() {
        AnalysisController ac = new AnalysisController();
        ac.setFilename(BASEDIR + "qav-analysis-result.json");
        ac.init();

        List<ImageDTO> images = ac.getImages();
        assertThat(images).hasSize(3);
        assertThat(images.get(0).getImageName()).isEqualTo("packageGraph");
        assertThat(images.get(1).getImageName()).isEqualTo("packageCycleGraph");
        assertThat(images.get(2).getImageName()).isEqualTo("legend");
    }

    @Test
    public void testGetImage() throws IOException {
        AnalysisController ac = new AnalysisController();
        ac.setFilename(BASEDIR + "qav-analysis-result.json");
        ac.init();

        ResponseEntity<Resource> img = ac.getImage("p1.png");
        assertThat(img).isNotNull();
        assertThat(img.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(img.getBody()).isNotNull();
        assertThat(img.getBody().contentLength()).isEqualTo(4679L);
    }

    @Test
    public void testGetImageNotFound() {
        AnalysisController ac = new AnalysisController();
        ac.setFilename(BASEDIR + "qav-analysis-result.json");
        ac.init();

        try {
            ac.getImage("not-found.png");
            fail("NotFoundException expected");
        } catch (NotFoundException e) {
            assertThat(e.getMessage()).isEqualTo("Image not found: not-found.png");
        }
    }
}