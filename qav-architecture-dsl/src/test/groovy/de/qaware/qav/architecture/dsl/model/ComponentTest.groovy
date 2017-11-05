package de.qaware.qav.architecture.dsl.model

import org.junit.Test

/**
 * Tests for {@link Component}.
 *
 * @author QAware GmbH
 */
class ComponentTest {

    @Test
    void testAllUses() {
        Component component = new Component()

        component.uses["A"] = new ClassSet("A", ["a", "b", "c"])
        component.uses["B"] = new ClassSet("B", ["c", "d", "e"])

        assert component.allUsesComponents() == ["a", "b", "c", "c", "d", "e"]
    }

    @Test
    void testAllUsesNoInput() {
        Component component = new Component()
        assert component.allUsesComponents() == []
    }

    @Test
    void testGetApiName() {
        Component component = new Component()
        component.api["A"] = new ClassSet("A", ["a.*", "b"])
        component.api["B"] = new ClassSet("B", ["c.*", "d.*", "e"])

        assert component.getApiName("a.my.example.Class") == "A"
        assert component.getApiName("b") == "A"
        assert component.getApiName("c") == "B"
        assert component.getApiName("f") == null
    }
}
