package de.qaware.qav.server.model;

import lombok.Data;
import lombok.ToString;

/**
 * DTO with infos about the Graph.
 *
 * Infos to be displayed at in the QAvalidator Web UI.
 */
@Data
@ToString
public class GraphInfoDTO {

    private String info;
}
