package de.qaware.qav.analysis.result.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for {@link ResultJsonUtil}
 */
public class ResultJsonUtilTest {

    @Test
    public void testTimestampFormat() throws JsonProcessingException {
        ObjectMapper mapper = ResultJsonUtil.initJsonMapper();

        String s = mapper.writeValueAsString(new MyClass());
        assertThat(s).contains("\"dateTime\" : \"2019-02-04T15:13:24.000000933\""); // just check the time format; avoid checking the line separators
    }

    public class MyClass {
        private LocalDateTime dateTime = LocalDateTime.of(2019, 2, 4, 15, 13, 24, 933);

        public LocalDateTime getDateTime() {
            return dateTime;
        }

        public void setDateTime(LocalDateTime dateTime) {
            this.dateTime = dateTime;
        }
    }
}