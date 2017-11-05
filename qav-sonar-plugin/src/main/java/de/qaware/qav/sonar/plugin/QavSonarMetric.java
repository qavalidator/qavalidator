package de.qaware.qav.sonar.plugin;

import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;

import java.util.Arrays;
import java.util.List;

/**
 * QAvalidator Metrics definition.
 * <p>
 * There is one metric for QAvalidator Errors, and one for Warnings.
 * They show up in the section for "Design".
 * <p>
 * The metrics are used in the QAware Quality Contract / Quality Gateway.
 *
 * @author QAware GmbH
 */
public class QavSonarMetric implements Metrics {

    /**
     * Architecture violations (errors)
     */
    public static final Metric<Integer> QAV_SONAR_ERRORS =
            new Metric.Builder("qav_sonar_errors", "QAvalidator Errors", Metric.ValueType.INT)
                    .setDescription("QAvalidator violations")
                    .setDirection(Metric.DIRECTION_WORST)
                    .setQualitative(false)
                    .setDomain(CoreMetrics.DOMAIN_DESIGN)
                    .create();

    /**
     * Architecture violations (warnings)
     */
    public static final Metric<Integer> QAV_SONAR_WARNINGS =
            new Metric.Builder(
                    "qav_sonar_warnings", "QAvalidator Warnings", Metric.ValueType.INT)
                    .setDescription("QAvalidator warnings")
                    .setDirection(Metric.DIRECTION_WORST)
                    .setQualitative(false)
                    .setDomain(CoreMetrics.DOMAIN_DESIGN)
                    .create();


    @Override
    public List<Metric> getMetrics() {
        return Arrays.asList(QAV_SONAR_ERRORS, QAV_SONAR_WARNINGS);
    }
}
