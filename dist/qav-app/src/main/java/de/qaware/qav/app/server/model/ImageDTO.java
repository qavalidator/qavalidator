package de.qaware.qav.app.server.model;

import lombok.Data;

/**
 *
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
