package de.qaware.qav.util;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link StringTemplateUtil}.
 *
 * @author QAware GmbH
 */
public class StringTemplateUtilTest {

    @Test
    public void testLoadTemplateGroup() {
        StringTemplateGroup stringTemplateGroup = StringTemplateUtil.loadTemplateGroup("/stg/Test.stg");
        assertStringTemplate(stringTemplateGroup);
    }

    @Test
    public void testLoadTemplateGroupWithDollarSign() {
        StringTemplateGroup stringTemplateGroup = StringTemplateUtil.loadTemplateGroup("/stg/Test_Dollar.stg", true);
        assertStringTemplate(stringTemplateGroup);
    }

    private void assertStringTemplate(StringTemplateGroup stringTemplateGroup) {
        assertNotNull(stringTemplateGroup);
        assertThat(stringTemplateGroup.getName(), is("TEST"));
        StringTemplate someElement = stringTemplateGroup.getTemplateDefinition("someElement");
        someElement.setAttribute("name", "Peter");
        assertThat(someElement.toString(), is("Hello Peter!"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLoadTemplateGroupNotExisting() {
        StringTemplateUtil.loadTemplateGroup("/not/existing/file.stg");
    }
}