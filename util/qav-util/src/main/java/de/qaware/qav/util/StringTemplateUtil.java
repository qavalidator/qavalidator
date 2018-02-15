package de.qaware.qav.util;

import com.google.common.base.Charsets;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.antlr.stringtemplate.language.DefaultTemplateLexer;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Util methods to deal with ANTLR StringTemplates.
 *
 * @author tilman
 */
public final class StringTemplateUtil {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(StringTemplateUtil.class);

    /**
     * util class with only static methods.
     */
    private StringTemplateUtil() {
    }

    /**
     * loads the STG file; that should be on the classpath (i.e. could also be in the resources or jar file)
     *
     * @param templateName the name of the template file
     * @return the {@link StringTemplateGroup}
     */
    public static StringTemplateGroup loadTemplateGroup(String templateName) {
        return loadTemplateGroupAngleBracket(templateName);
    }

    /**
     * Loads the STG file; that should be on the classpath (i.e. could also be in the resources or jar file).
     * The STG file uses "&lt;" / "&gt;" characters.
     *
     * @param templateName  the name of the template file
     * @return the {@link StringTemplateGroup}
     */
    public static StringTemplateGroup loadTemplateGroupAngleBracket(String templateName) {
        InputStreamReader reader = getTemplateInputStreamReader(templateName);
        StringTemplateGroup result = new StringTemplateGroup(reader);
        IOUtils.closeQuietly(reader);

        return result;
    }

    /**
     * Loads the STG file; that should be on the classpath (i.e. could also be in the resources or jar file).
     * The STG file uses "$" instead of "&lt;" / "&gt;", which is handy for HTML templates.
     *
     * @param templateName  the name of the template file
     * @return the {@link StringTemplateGroup}
     */
    public static StringTemplateGroup loadTemplateGroupDollarSign(String templateName) {
        InputStreamReader reader = getTemplateInputStreamReader(templateName);
        StringTemplateGroup result = new StringTemplateGroup(reader, DefaultTemplateLexer.class);
        IOUtils.closeQuietly(reader);

        return result;
    }

    private static InputStreamReader getTemplateInputStreamReader(String templateName) {
        InputStream is = StringTemplateUtil.class.getResourceAsStream(templateName);

        if (is == null) {
            String msg = "Resource '" + templateName + "' not found.";
            LOGGER.error(msg);
            throw new IllegalArgumentException(msg);
        }

        return new InputStreamReader(is, Charsets.UTF_8);
    }
}
