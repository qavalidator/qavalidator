package de.qaware.qav.test.inner;


/**
 * Test class to test the dependency analysis.
 *
 * @author QAware GmbH
 */
public class MyInner {

    public int f() {
        return new Inner().g();
    }

    public class Inner {
        public int g() {
            A a = new A();
            return a.isString(a) ? 0 : 1;
        }
    }
}
