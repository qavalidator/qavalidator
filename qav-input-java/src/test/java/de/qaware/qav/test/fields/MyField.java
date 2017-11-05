package de.qaware.qav.test.fields;

import java.util.List;
import java.util.Map;

/**
 * @author QAware GmbH
 */
public class MyField {

    private A a; // do nothing with it.

    private B b; // access a normal field directly.

    // class C: access a static field directly, see method g()

    private List<D> dList;

    private List<Map<String, List<F>>> fMap; // reference to deeply nested types

    public void f() {
        b.t = "yes";
    }

    public void g() {
        C.s = "hello";
    }

    public void h(E e) {
        String s = e.s;
    }
}
