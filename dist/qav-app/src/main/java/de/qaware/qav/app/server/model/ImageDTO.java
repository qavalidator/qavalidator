package de.qaware.qav.app.server.model;

import lombok.Data;

/**
 * DTO with description of displayable images.
 *
 * Infos to be displayed at in the QAvalidator Web UI.
 */
@Data
public class ImageDTO {

    private String imageName;

    private String filenamePNG;
    private String filenameSVG;
    private String filenameGraphml;

    private Integer noNodes;
    private Integer noEdges;
}
