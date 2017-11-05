package de.qaware.qav.doc;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Describes QAvalidator Analysis DSL plugins.
 *
 * @author QAware GmbH
 */
@Retention(RetentionPolicy.SOURCE)
public @interface QavPluginDoc {

    /**
     * Name of the plugin.
     *
     * @return the name of the plugin
     */
    String name();

    /**
     * Name of the description.
     *
     * @return the name of the description
     */
    String description() default "";
}
