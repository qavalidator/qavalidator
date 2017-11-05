package de.qaware.qav.test.generics;

import java.util.List;

/**
 * @author QAware GmbH
 */
public class MyGenericsMethods {

    public List<String> filterList(List<A> list) {
        return null;
    }

    public List<String> f(List<List<B>> doubleList) {
        return null;
    }


    public <V> V getX(List<V> list) {
        return list.get(0);
    }

    public <U extends C> U getY(List<U> list) {
        return list.get(0);
    }
}
