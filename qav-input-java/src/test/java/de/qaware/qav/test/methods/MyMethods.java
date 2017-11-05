package de.qaware.qav.test.methods;

import java.util.ArrayList;
import java.util.List;

/**
 * @author QAware GmbH
 */
public class MyMethods {

    public void readA(A a) {
    }

    public B readB() {
        return null;
    }

    public int countC(List<C> list) {
        return list.size();
    }

    public List<D> getList() {
        return new ArrayList<>();
    }

    public int getResult() {
        return E.getNumber();
    }

    public int getX(IF ifc) {
        return ifc.count(new G());
    }

    public int getX(IH h) {
        return 0;
    }
}
