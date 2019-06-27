package de.qaware.qav.architecture.dsl.model

import org.junit.Test

/**
 * Tests for {@link Component}.
 *
 * @author QAware GmbH
 */
class ComponentTest {

    // --- toString

    @Test
    void testToString() {
        Component component = new Component(
                name: "C1",
                api: ["A.api": new ClassSet("A", ["a.*", "b"])],
                impl: ["A.impl": new ClassSet("B", ["a.impl.*"])],
                pathSeparator: "/"
        )

        Component child1 = new Component(name: "child1")
        Component child2 = new Component(name: "child2")
        component.children << child1
        component.children << child2
        child1.parent = component
        child2.parent = component

        assert component.toString() == "Component(name:C1, " +
                "children:[" +
                "Component(name:child1, children:[], api:[:], impl:[:], usesAPI:[:], usesImpl:[:], pathSeparator:/), " +
                "Component(name:child2, children:[], api:[:], impl:[:], usesAPI:[:], usesImpl:[:], pathSeparator:/)], " +
                "api:[A.api:ClassSet(name:A, patterns:[a.*, b])], " +
                "impl:[A.impl:ClassSet(name:B, patterns:[a.impl.*])], " +
                "usesAPI:[:], " +
                "usesImpl:[:], " +
                "pathSeparator:/)"
    }

    // --- getApiName()

    @Test
    void testGetApiName() {
        Component component = new Component()
        component.api["A"] = new ClassSet("A", ["a.**", "b"])
        component.api["B"] = new ClassSet("B", ["c.**", "d.*", "e"])

        assert component.getApiName("a.my.example.Class") == "A"
        assert component.getApiName("b") == "A"
        assert component.getApiName("c") == "B"
        assert component.getApiName("f") == null
    }

    // --- getImplName()

    @Test
    void getImplName() {
        Component component = new Component()
        component.impl["A"] = new ClassSet("A", ["a.**", "b"])
        component.impl["B"] = new ClassSet("B", ["c.**", "d.*", "e"])

        assert component.getImplName("a.my.example.Class") == "A"
        assert component.getImplName("b") == "A"
        assert component.getImplName("c") == "B"
        assert component.getImplName("f") == null
    }

    // --- isApi() and isImpl()

    @Test
    void isApi() {
        Component component = new Component()
        component.api["A"] = new ClassSet("A", ["a.*", "b"])
        component.api["B"] = new ClassSet("B", ["c.*", "d.*", "e"])

        assert component.isApi("a.A1")
        assert !component.isApi("f.A1")
    }

    @Test
    void isImpl() {
        Component component = new Component()
        component.impl["A"] = new ClassSet("A", ["a.*", "b"])
        component.impl["B"] = new ClassSet("B", ["c.*", "d.*", "e"])

        assert component.isImpl("a.A1")
        assert !component.isImpl("f.A1")
    }

    // --- getPathSeparator()

    @Test
    void getPathSeparator1() {
        Component component = new Component()
        assert component.pathSeparator == null
    }

    @Test
    void getPathSeparator2() {
        Component component = new Component(pathSeparator: "/")
        assert component.pathSeparator == "/"
    }

    @Test
    void getPathSeparator3() {
        Component c1 = new Component()
        assert c1.pathSeparator == null

        Component c2 = new Component(pathSeparator: "/")
        c1.parent = c2

        assert c1.pathSeparator == "/"
    }

    // --- allUsesAPIs()

    @Test
    void testAllUsesApi() {
        Component component = new Component()

        component.usesAPI["A"] = new ClassSet("A", ["a", "b", "c"])
        component.usesAPI["B"] = new ClassSet("B", ["c", "d", "e"])

        assert component.allUsesAPIs() == ["a", "b", "c", "c", "d", "e"]
    }

    @Test
    void testAllUsesApiNoInput() {
        Component component = new Component()
        assert component.allUsesAPIs() == []
    }

    // --- allUsesImpl()

    @Test
    void testAllUsesImpl() {
        Component component = new Component()

        component.usesImpl["A"] = new ClassSet("A", ["a", "b", "c"])
        component.usesImpl["B"] = new ClassSet("B", ["c", "d", "e"])

        assert component.allUsesImpl() == ["a", "b", "c", "c", "d", "e"]
    }
}
