package de.qaware.qav.util;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

/**
 * Tests for {@link ProcessUtil}.
 *
 * @author QAware GmbH
 */
public class ProcessUtilTest {

    @Test
    public void execProcess() {
        List<String> commandLine = Lists.newArrayList("java", "-version");
        int result = ProcessUtil.execProcess(".", commandLine);

        assertThat(result, is(0));
    }

    @Test
    public void execProcessWithErrors() {
        List<String> commandLine = Lists.newArrayList("java", "-wrongArgument");
        int result = ProcessUtil.execProcess(".", commandLine);

        assertThat(result, is(1));
    }

    @Test
    public void execProcessWithWrongCommand() {
        List<String> commandLine = Lists.newArrayList("not-existing-command", "-wrongArgument");
        int result = ProcessUtil.execProcess(".", commandLine);

        assertThat(result, is(-1));
    }

    @Test
    public void execProcessInBackground() {
        List<String> commandLine = Lists.newArrayList("java", "-version");
        ProcessUtil.execProcessInBackground(".", commandLine);
        ProcessUtil.finish();
    }

    @Test
    public void execProcessInBackgroundTwoCommands() {
        List<String> commandLine = Lists.newArrayList("java", "-version");

        // call it twice
        ProcessUtil.execProcessInBackground(".", commandLine);
        ProcessUtil.execProcessInBackground(".", commandLine);

        ProcessUtil.finish();

        // calling finish more than once doesn't hurt.
        ProcessUtil.finish();
    }
}