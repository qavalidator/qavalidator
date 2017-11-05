package de.qaware.qav.sonar.plugin;

import org.junit.Test;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.internal.DefaultSensorDescriptor;
import org.sonar.api.batch.sensor.internal.SensorContextTester;

import java.io.File;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

/**
 * Tests for {@link QavSonarSensor}.
 *
 * @author QAware GmbH
 */
public class QavSonarSensorTest {

    @Test
    public void testDescribe() {
        QavSonarSensor sensor = new QavSonarSensor();
        DefaultSensorDescriptor sensorDescriptor = new DefaultSensorDescriptor();
        sensor.describe(sensorDescriptor);

        assertThat(sensorDescriptor.name(), is("QAvalidator Sonar sensor"));
    }

    @Test
    public void testQavSonarSensor() {
        QavSonarSensor sensor = new QavSonarSensor();
        SensorContext sensorContext = SensorContextTester.create(new File("src/test/resources/logTest2"));
        sensor.execute(sensorContext);
    }
}