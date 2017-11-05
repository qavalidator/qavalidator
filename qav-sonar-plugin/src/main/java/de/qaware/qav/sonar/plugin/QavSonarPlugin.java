package de.qaware.qav.sonar.plugin;

import org.sonar.api.Plugin;

/**
 * The QAvalidator Sonarqube plugin.
 * <p>
 * Defines the metric and the sensor.
 *
 * @author QAware GmbH
 */
public class QavSonarPlugin implements Plugin {

    @Override
    public void define(Context context) {
        context.addExtensions(QavSonarMetric.class, QavSonarSensor.class);
    }
}
