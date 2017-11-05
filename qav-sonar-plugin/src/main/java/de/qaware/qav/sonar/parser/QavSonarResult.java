package de.qaware.qav.sonar.parser;

import com.google.common.base.MoreObjects;

/**
 * Bean for QAvalidator Sonarqube results:
 *
 * Number of warnings, and number of errors,
 * and a flag indicating whether there is a QAvalidator result at all.
 *
 * @author QAware GmbH
 */
public class QavSonarResult {

    private boolean isEmpty = false;
    private int noWarnings;
    private int noErrors;

    public boolean isEmpty() {
        return isEmpty;
    }

    /**
     * Setter.
     *
     * "empty" means: "there are no QAvalidator result files at all."
     *
     * @param empty the new value
     */
    public void setEmpty(boolean empty) {
        isEmpty = empty;
    }

    public int getNoWarnings() {
        return noWarnings;
    }

    public void setNoWarnings(int noWarnings) {
        this.noWarnings = noWarnings;
    }

    public int getNoErrors() {
        return noErrors;
    }

    public void setNoErrors(int noErrors) {
        this.noErrors = noErrors;
    }

    /**
     * increment the number of warnings
     */
    public void incWarnings() {
        noWarnings++;
    }

    /**
     * increment the number of errors
     */
    public void incErrors() {
        noErrors++;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("isEmpty", isEmpty)
                .add("noWarnings", noWarnings)
                .add("noErrors", noErrors)
                .toString();
    }
}

