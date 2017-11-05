package de.qaware.qav.test.exceptions;

import java.io.IOException;

/**
 * @author QAware GmbH
 */
public class MyClass {

    public int f() throws IOException {
        return 0;
    }

    public int g() {
        throw new MyRTE1("error");
    }

    public int h() {
        try {
            boolean a = true;
        } catch(MyRTE2 e) {
            return -1;
        }

        return 0;
    }
}
