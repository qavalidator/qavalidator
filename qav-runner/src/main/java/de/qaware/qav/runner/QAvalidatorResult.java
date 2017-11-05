package de.qaware.qav.runner;

import com.google.common.base.MoreObjects;

import java.util.ArrayList;
import java.util.List;

/**
 * Result bean for a QAvalidator analysis run.
 *
 * The idea is that the {@link QAvalidator} does all the exception handling; therefore this bean contains information
 * about any exception that was thrown in the analysis run.
 *
 * @author QAware GmbH
 */
public class QAvalidatorResult {

    private List<String> failedSteps = new ArrayList<>();

    private boolean failedWithException = false;

    private String exceptionMessage;

    // ----- getters and setters

    /**
     * Getter.
     *
     * @return the list of failed steps. May be empty, never null.
     */
    public List<String> getFailedSteps() {
        return failedSteps;
    }

    /**
     * setter.
     *
     * @param failedSteps list of failed steps.
     */
    public void setFailedSteps(List<String> failedSteps) {
        this.failedSteps.clear();
        if (failedSteps != null) {
            this.failedSteps.addAll(failedSteps);
        }
    }

    public boolean isFailedWithException() {
        return failedWithException;
    }

    public void setFailedWithException(boolean failedWithException) {
        this.failedWithException = failedWithException;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("failedSteps", failedSteps)
                .add("failedWithException", failedWithException)
                .add("exceptionMessage", exceptionMessage)
                .toString();
    }
}
