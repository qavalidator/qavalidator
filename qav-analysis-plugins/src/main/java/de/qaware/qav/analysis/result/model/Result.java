package de.qaware.qav.analysis.result.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Represents one result item, like an image or the graph output file.
 */
@Data
@NoArgsConstructor // needed for the JSON reader
@RequiredArgsConstructor
@AllArgsConstructor
public class Result {

    @NonNull
    private ResultType resultType;

    /**
     * The file name, or the filename base (in case of images, where the base will have endings: .png, .svg., .dot,
     * .graphml)
     */
    @NonNull
    private String filename;

    /** size of the represented graph; may be null if no size is given. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer noNodes;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer noEdges;
}
