package de.qaware.qav.app.server.mapper;

import de.qaware.qav.analysis.result.model.Result;
import de.qaware.qav.app.server.model.ImageDTO;

import java.io.File;
import java.util.Objects;

/**
 * Maps {@link Result} entities into {@link ImageDTO} DTOs.
 */
public final class ImageMapper {

    /** no instances. */
    private ImageMapper() {
    }

    /**
     * Maps a {@link Result} into a {@link ImageDTO}.
     *
     * @param image the {@link Result}. May not be null.
     * @return the {@link ImageDTO}
     */
    public static ImageDTO toDTO(String baseDir, Result image) {
        Objects.requireNonNull(image, "image");

        ImageDTO imageDTO = new ImageDTO();

        imageDTO.setImageName(image.getFilename());

        imageDTO.setFilenamePNG(fileIfExists(baseDir, image.getFilename() + ".png"));
        imageDTO.setFilenameSVG(fileIfExists(baseDir, image.getFilename() + ".svg"));
        imageDTO.setFilenameGraphml(fileIfExists(baseDir, image.getFilename() + ".graphml"));

        imageDTO.setNoNodes(image.getNoNodes());
        imageDTO.setNoEdges(image.getNoEdges());

        return imageDTO;
    }

    private static String fileIfExists(String baseDir, String filename) {
        return new File(baseDir, filename).exists() ? filename : null;
    }
}
