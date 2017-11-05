package de.qaware.qav.sonar.plugin;

import de.qaware.qav.sonar.parser.LogFileParser;
import de.qaware.qav.sonar.parser.QavSonarResult;
import org.slf4j.Logger;
import org.sonar.api.batch.fs.internal.DefaultInputModule;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;

import java.io.File;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * QAvalidator Sensor.
 * <p>
 * Checks for QAvalidator/Sonarqube files, and counts the warnings and errors in those files.
 * Only if there is a result file, values are stored at all.
 *
 * @author QAware GmbH
 */
public class QavSonarSensor implements Sensor {

    private static final Logger LOGGER = getLogger(QavSonarSensor.class);

    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor.name("QAvalidator Sonar sensor");
    }

    @Override
    public void execute(SensorContext context) {
        File baseDir = context.fileSystem().baseDir();
        QavSonarResult result = LogFileParser.analyse(baseDir);

        String moduleKey = context.module().key();
        LOGGER.info("QAvalidator result: {}, path: {}, module key: {}", result, baseDir.getAbsolutePath(), moduleKey);

        if (!result.isEmpty()) {
            saveResult(context, result, moduleKey);
        } else {
            LOGGER.error("No QAvalidator result for module {}", moduleKey);
        }
    }

    /**
     * Store the results into SonarQube.
     *
     * The results are stored by module. SonarQube will aggregate the values.
     * Only if there is a result file, values are stored at all.
     *
     * @param context   the context to save the result to
     * @param result    the result
     * @param moduleKey the module key
     */
    private void saveResult(SensorContext context, QavSonarResult result, String moduleKey) {
        DefaultInputModule inputModule = new DefaultInputModule(moduleKey);

        context.<Integer>newMeasure()
                .forMetric(QavSonarMetric.QAV_SONAR_ERRORS)
                .withValue(result.getNoErrors())
                .on(inputModule)
                .save();

        context.<Integer>newMeasure()
                .forMetric(QavSonarMetric.QAV_SONAR_WARNINGS)
                .withValue(result.getNoWarnings())
                .on(inputModule)
                .save();
    }
}
