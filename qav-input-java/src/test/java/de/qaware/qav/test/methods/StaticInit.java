package de.qaware.qav.test.methods;

import java.util.Locale;

/**
 * @author QAware GmbH
 */
public class StaticInit {

    public static String s;

    static {
        s = "yes".toUpperCase(Locale.ENGLISH);
    }
}
