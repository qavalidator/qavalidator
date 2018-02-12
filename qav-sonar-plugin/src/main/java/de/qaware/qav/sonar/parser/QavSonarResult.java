package de.qaware.qav.sonar.parser;

import lombok.Data;
import lombok.ToString;

/**
 * Bean for QAvalidator Sonarqube results:
 *
 * Number of warnings, and number of errors,
 * and a flag indicating whether there is a QAvalidator result at all.
 *
 * @author QAware GmbH
 */
@Data
@ToString
public class QavSonarResult {

    /**
     * "empty" means: "there are no QAvalidator result files at all."
     */
    private boolean isEmpty = false;
    private int noWarnings;
    private int noErrors;

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
}

