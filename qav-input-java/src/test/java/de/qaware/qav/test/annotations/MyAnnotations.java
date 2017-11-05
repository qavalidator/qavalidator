package de.qaware.qav.test.annotations;

/**
 * @author QAware GmbH
 */
@A
public class MyAnnotations {

    @B
    private int b;

    @C(name = "this is c")
    private String c;

    @D
    public int f() {
        return 1;
    }

    @E
    public int g(@F int param) {
        return 2 * param;
    }
}
