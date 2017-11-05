package de.qaware.qav.test.generics;

import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author QAware GmbH
 */
public class MyGenericsFields {

    private Set<A> setOfA; // do nothing with this.

    private Set<B> setOfB = new HashSet<>();

    private final static Set<C> setOfC;

    private List<List<D>> doubleList;

    static {
        setOfC = Sets.newHashSet();
    }

}
