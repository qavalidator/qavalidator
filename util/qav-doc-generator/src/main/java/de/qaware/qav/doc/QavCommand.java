package de.qaware.qav.doc;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Describes QAvalidator Analysis DSL methods.
 *
 * @author QAware GmbH
 */
@Retention(RetentionPolicy.SOURCE)
public @interface QavCommand {

    /**
     * Name of the command
     *
     * @return the name of the command
     */
    String name();

    /**
     * Description of the command
     *
     * @return the description of the command
     */
    String description() default "";

    /**
     * Parameters
     *
     * @return the parameters
     */
    Param[] parameters() default {};

    /**
     * Result
     *
     * @return the result
     */
    String result() default "";
    
    /**
     * Parameters to the commands
     */
    @interface Param {

        /**
         * The name of the parameter.
         *
         * @return the name of the parameter.
         */
        String name();

        /**
         * The description of the parameter.
         *
         * @return the description of the parameter.
         */
        String description();
    }
}
