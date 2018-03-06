package de.qaware.qav.architecture.dsl.model

import org.junit.Before
import org.junit.Test

/**
 * Tests for {@link Architecture}.
 *
 * @author QAware GmbH
 */
class ArchitectureTest {

    private Architecture architecture

    @Before
    void init() {
        this.architecture = new Architecture()
    }

    // --- toString()

    @Test
    void testToString() {
        architecture.prefix = "T"
        architecture.reflexMLversion = "1.0"

        architecture.includes = [ "api" : new ClassSet("api", ["com.my.**"])]
        architecture.excludes = [ "unwanted" : new ClassSet("api", ["com.my.util.**"])]

        assert architecture.toString() == "Architecture[prefix=T,reflexMLversion=1.0," +
                "includes={api=ClassSet[name=api,patterns=[com.my.**]]}," +
                "excludes={unwanted=ClassSet[name=api,patterns=[com.my.util.**]]}]"
    }

    // --- getParentComponent()

    @Test(expected = NullPointerException)
    void getParentComponent1() {
        architecture.getParentComponent(null)
    }

    @Test
    void getParentComponent2() {
        architecture.name = "myArch"
        assert architecture.getParentComponent("myArch") == null
    }

    @Test
    void getParentComponent3() {
        architecture.includes = [ "api" : new ClassSet("api", ["v*"])]

        // name is not included => no component in this Architecture
        assert architecture.getParentComponent("x1") == null
        assert architecture.getParentComponentName("x1") == null
    }

    @Test
    void getParentComponent4() {
        Component c1 = new Component()
        c1.name = "C1"
        Component p1 = new Component()
        p1.name = "P1"
        p1.children << c1
        c1.parent = p1
        architecture.nameToComponent[c1.name] = c1

        assert architecture.getParentComponent("C1") == p1
        assert architecture.getParentComponent("C1") == p1 // do it twice to test the caching
        assert architecture.getParentComponentName("C1") == "P1"
    }

    @Test
    void getParentComponent5() {
        Component c1 = new Component()
        c1.name = "C1"
        c1.api["C1.api"] = new ClassSet("C1.api", ["com.my.**"])
        architecture.allComponents << c1

        assert c1.isApi("com.my.app.A1")
        assert architecture.getParentComponent("com.my.app.A1") == c1
        assert architecture.getParentComponentName("com.my.app.A1") == "C1"
    }

    @Test
    void getParentComponent6() {
        Component c1 = new Component()
        c1.name = "C1"
        c1.impl["C1.impl"] = new ClassSet("C1.api", ["com.my.**"])
        architecture.allComponents << c1

        assert c1.isImpl("com.my.app.A1")
        assert architecture.getParentComponent("com.my.app.A1") == c1
        assert architecture.getParentComponentName("com.my.app.A1") == "C1"
    }

    @Test
    void getParentComponent7() {
        architecture.includes = [ "api" : new ClassSet("api", ["com.my.**"])]

        Component c1 = new Component()
        c1.name = "C1"
        c1.api["C1.api"] = new ClassSet("C1.api", ["com.my.app.**"])
        architecture.allComponents << c1

        assert architecture.isIncluded("com.my.util.A1")
        assert !c1.isApi("com.my.util.A1")
        assert !c1.isImpl("com.my.util.A1")
        assert architecture.getParentComponent("com.my.util.A1") == null
    }

    // --- isIncluded()

    @Test
    void isIncluded1() {
        assert architecture.isIncluded("myName")
    }

    @Test
    void isIncluded2() {
        architecture.includes = [ "api" : new ClassSet("api", ["v*"])]

        assert architecture.isIncluded("v1")
        assert !architecture.isIncluded("x1")
    }

    @Test
    void isIncluded3() {
        architecture.excludes = [ "api" : new ClassSet("api", ["v*"])]

        assert !architecture.isIncluded("v1")
        assert architecture.isIncluded("x1")
    }

    @Test
    void isIncluded4() {
        architecture.includes = [ "api" : new ClassSet("api", ["v.api.*"])]
        architecture.excludes = [ "api" : new ClassSet("api", ["v.impl.*"])]

        assert architecture.isIncluded("v.api.A")
        assert !architecture.isIncluded("v.impl.A")
        assert !architecture.isIncluded("other")
    }

    @Test
    void isIncluded5() {
        architecture.includes = [ "api" : new ClassSet("api", ["v*"])]
        architecture.excludes = [ "api" : new ClassSet("api", ["v*"])]

        assert !architecture.isIncluded("v1")
        assert !architecture.isIncluded("other")
    }

}