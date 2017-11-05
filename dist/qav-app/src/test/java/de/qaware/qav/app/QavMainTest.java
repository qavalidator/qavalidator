package de.qaware.qav.app;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Tests for {@link QavMain}.
 *
 * @author QAware GmbH
 */
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest
public class QavMainTest {

    @Autowired
    private QavMain qavMain;

    @Test
    public void testOptionUsage() {
        assert qavMain != null;
    }
}
