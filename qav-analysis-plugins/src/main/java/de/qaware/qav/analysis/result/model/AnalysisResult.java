package de.qaware.qav.analysis.result.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an analysis result.
 */
@Data
public class AnalysisResult {

    private String analysisId;
    private LocalDateTime analysisStart;

    private String baseDir;
    private List<Result> items = new ArrayList<>();

    /**
     * Add the given {@link Result} item to the list.
     *
     * @param result the new {@link Result}
     */
    public void addResult(Result result) {
        items.add(result);
    }
}
