package de.qaware.qav.app.server.controller;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * Tests for {@link InputSanitizer}.
 */
public class InputSanitizerTest {

    @Test
    public void testValidateImageName() {
        assertValid("xx.png");
        assertValid("xx.svg");
        assertValid("xx");
        assertValid("xx.v1.png");
        assertValid("xx_v1.png");
        assertValid("xx-v1.png");
        assertValid("dir/xx.png");
        assertValid("dir//xx.png");

        assertInvalid("dir/../xx.png");
        assertInvalid("../xx.png");
    }

    private void assertValid(String filename) {
        assertThat(InputSanitizer.validateImageName(filename)).isEqualTo(filename);
    }

    private void assertInvalid(String filename) {
        try {
            InputSanitizer.validateImageName(filename);
            fail("IllegalArgumentException expected");
        } catch(IllegalArgumentException e) {
            assertThat(e.getMessage()).isEqualTo("invalid image name: " + filename);
        }
    }

}