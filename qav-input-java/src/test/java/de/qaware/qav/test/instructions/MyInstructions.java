package de.qaware.qav.test.instructions;

/**
 * @author QAware GmbH
 */
public class MyInstructions {

    public boolean isString(A a) {
        return a instanceof B;
    }

    public int getX() {
        return new C().i;
    }

    public int getY() {
        for (int i = 0; i < 10; i++) {
            if (i == 7) {
                return new D().getX();
            }
        }
        return 0;
    }

}
