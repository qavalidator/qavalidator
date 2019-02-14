package de.qaware.qav.app.server.controller;

import com.google.common.base.Strings;
import de.qaware.qav.analysis.result.api.AnalysisResultReader;
import de.qaware.qav.analysis.result.api.AnalysisResultWriter;
import de.qaware.qav.analysis.result.model.AnalysisResult;
import de.qaware.qav.analysis.result.model.ResultType;
import de.qaware.qav.app.server.exceptions.NotFoundException;
import de.qaware.qav.app.server.mapper.ImageMapper;
import de.qaware.qav.app.server.model.ImageDTO;
import de.qaware.qav.util.FileNameUtil;
import de.qaware.qav.util.FileSystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller to access the analysis results.
 * <p>
 * Reads the analysis result JSON file and keeps it in memory.
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class AnalysisController {

    private String filename;
    private String graphFilename;
    private AnalysisResult analysisResult;

    /**
     * Setter.
     *
     * @param filename the filename of the analysis result file.
     */
    @Value("${de.qaware.qav.analysis.filename}")
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * Setter.
     *
     * @param graphFilename the filename of the dependency graph to read. Used as a fallback strategy if no filename for
     *                      the analysis is provided.
     */
    @Value("${de.qaware.qav.graph.filename}")
    public void setGraphFilename(String graphFilename) {
        this.graphFilename = graphFilename;
    }

    /**
     * Find and read the analysis result file.
     * <p>
     * Uses the given {@link #filename}. If that is not given, then try to use the {@link #graphFilename}, if it is
     * given, to derive the analysis result file name.
     * <p>
     * If the analysis file name is given but can't be found, this method throws an exception (causing the server not to
     * start at all).
     * <p>
     * If the analysis file name can't be derived, this method does not throw an exception, so that the server starts
     * and the other functionality can be used.
     */
    @PostConstruct
    public void init() {
        File file;
        if (!Strings.isNullOrEmpty(filename)) {
            LOGGER.info("Reading analysis results from {}", FileNameUtil.getCanonicalPath(filename));
            FileSystemUtil.assertFileOrResourceExists(filename);
        } else if (!Strings.isNullOrEmpty(graphFilename)) {
            File graphFile = new File(graphFilename);
            file = new File(graphFile.getParentFile(), AnalysisResultWriter.QAV_RESULT_FILENAME);
            filename = file.getAbsolutePath();
            LOGGER.info("Fallback: Reading analysis results from {}", filename);

            if (!new File(filename).exists()) {
                filename = null;
            }
        }

        if (!Strings.isNullOrEmpty(filename)) {
            AnalysisResultReader analysisResultReader = new AnalysisResultReader(filename);
            this.analysisResult = analysisResultReader.getAnalysisResult();
        }
    }

    /**
     * REST endpoint to offer the overview over the analysis result.
     *
     * @return the {@link AnalysisResult}
     */
    @GetMapping(value = "/analysis")
    public AnalysisResult getAnalysisResult() {
        LOGGER.info("Get overview");
        assumeReady();
        return analysisResult;
    }

    /**
     * REST endpoint to offer the list of available images.
     *
     * @return the list of {@link ImageDTO}s
     */
    @GetMapping(value = "/analysis/images")
    public List<ImageDTO> getImages() {
        LOGGER.info("Get Images");
        assumeReady();

        return analysisResult.getItems().stream()
                .filter(item -> item.getResultType() == ResultType.IMAGE || item.getResultType() == ResultType.IMAGE_LEGEND)
                .map(image -> ImageMapper.toDTO(analysisResult.getBaseDir(), image))
                .collect(Collectors.toList());
    }

    /**
     * REST endpoint to offer an image.
     *
     * @param imageName the name of the image
     * @return the image
     */
    @GetMapping(value = "/analysis/images/{imageName}")
    @SuppressWarnings("squid:S2083") // Sonar does not believe that the input is checked. But it really is.
    public ResponseEntity<Resource> getImage(@PathVariable String imageName) {
        LOGGER.info("Get image {}", imageName);
        assumeReady();

        String sanitizedName = InputSanitizer.validateImageName(imageName);

        File file = new File(analysisResult.getBaseDir(), sanitizedName);
        if (!file.exists()) {
            throw new NotFoundException("Image not found: " + sanitizedName);
        }

        HttpHeaders httpHeaders = getHeaders(sanitizedName);
        return new ResponseEntity<>(new FileSystemResource(file), httpHeaders, HttpStatus.OK);
    }

    private HttpHeaders getHeaders(String imageName) {
        HttpHeaders httpHeaders = new HttpHeaders();
        String contentType = "image/png";
        if (imageName.endsWith(".png")) {
            contentType = "image/png";
        } else if (imageName.endsWith(".svg")) {
            contentType = "image/svg+xml";
        } else if (imageName.endsWith(".graphml")) {
            contentType = "application/octet-stream";
            httpHeaders.add("Content-Disposition", "attachment; filename=\"" + imageName + "\"");
        }
        httpHeaders.add("Content-Type", contentType);
        return httpHeaders;
    }

    /**
     * Checks if the preconditions are met and the analysis file was found. If not, throws a NotFoundException (i.e.
     * return 404).
     */
    private void assumeReady() {
        if (this.analysisResult == null) {
            throw new NotFoundException("Analysis file not found. No results being served.");
        }
    }
}
