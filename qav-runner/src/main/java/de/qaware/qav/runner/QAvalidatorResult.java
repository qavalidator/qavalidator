package de.qaware.qav.runner;

import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Result bean for a QAvalidator analysis run.
 * <p>
 * The idea is that the {@link QAvalidator} does all the exception handling; therefore this bean contains information
 * about any exception that was thrown in the analysis run.
 *
 * @author QAware GmbH
 */
@ToString
@Data
public class QAvalidatorResult {

    private List<String> failedSteps = new ArrayList<>();

    private boolean failedWithException = false;

    private String exceptionMessage;

    /**
     * Replaces the list of failed steps.
     *
     * @param failedSteps list of failed steps.
     */
    public void setFailedSteps(List<String> failedSteps) {
        this.failedSteps.clear();
        this.failedSteps.addAll(failedSteps);
    }
}
