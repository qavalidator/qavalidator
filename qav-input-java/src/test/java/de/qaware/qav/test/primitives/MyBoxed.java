package de.qaware.qav.test.primitives;

/**
 * To test that no relations to boxed types are put into the Dependency Graph.
 *
 * @author QAware GmbH
 */
public class MyBoxed {

    private Byte a;
    private Boolean b;
    private Character c;

    private Double d;
    private Float e;

    private Integer f;
    private Long g;
    private Short h;

    private Byte[][] aa;
    private Boolean[][] bb;
    private Character[][] cc;

    private Double[][] dd;
    private Float[][] ee;

    private Integer[][] ff;
    private Long[][] gg;
    private Short[][] hh;
}
