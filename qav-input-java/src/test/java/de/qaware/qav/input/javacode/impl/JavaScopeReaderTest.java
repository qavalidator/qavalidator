package de.qaware.qav.input.javacode.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.qaware.qav.architecture.dsl.model.Architecture;
import de.qaware.qav.architecture.factory.DefaultPackageArchitectureFactory;
import de.qaware.qav.graph.api.Dependency;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.DependencyType;
import de.qaware.qav.graph.api.Node;
import de.qaware.qav.graph.factory.DependencyGraphFactory;
import de.qaware.qav.test.annotations.MyAnnotations;
import de.qaware.qav.test.exceptions.MyClass;
import de.qaware.qav.test.fields.MyField;
import de.qaware.qav.test.generics.MyGenericsFields;
import de.qaware.qav.test.generics.MyGenericsMethods;
import de.qaware.qav.test.inheritance.MyInheritance;
import de.qaware.qav.test.inner.MyInner;
import de.qaware.qav.test.instructions.MyInstructions;
import de.qaware.qav.test.methods.MyMethods;
import de.qaware.qav.test.primitives.MyPrimitives;
import de.qaware.qav.test.reference.MyReference;
import de.qaware.qav.visualization.api.GraphExporter;
import de.qaware.qav.visualization.model.Abbreviation;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

/**
 * @author QAware GmbH
 */
public class JavaScopeReaderTest {

    public static final String TEST_CLASSES_ROOT = "build/classes/java/test";
    public static final String BUILD_TEST_OUTPUT_DIR = "build/test-output/";

    private DependencyGraph dependencyGraph;

    @Test
    public void testReadNoRootDirs() {
        dependencyGraph = DependencyGraphFactory.createGraph();
        new JavaScopeReader(dependencyGraph, true).read(Maps.newHashMap());

        assertThat(dependencyGraph.getAllNodes().size(), is(0));
    }

    @Test
    public void testAnnotations() {
        dependencyGraph = initJavaScopeReader(MyAnnotations.class, false);

        assertThat(dependencyGraph.getAllNodes().size(), is(7));

        Node my = getNode("de.qaware.qav.test.annotations.MyAnnotations");
        Node a = getNode("de.qaware.qav.test.annotations.A");
        Node b = getNode("de.qaware.qav.test.annotations.B");
        Node c = getNode("de.qaware.qav.test.annotations.C");
        Node d = getNode("de.qaware.qav.test.annotations.D");
        Node e = getNode("de.qaware.qav.test.annotations.E");
        Node f = getNode("de.qaware.qav.test.annotations.F");

        assertRelation(my, a, DependencyType.ANNOTATED_BY);
        assertRelation(my, b, DependencyType.ANNOTATED_BY);
        assertRelation(my, c, DependencyType.ANNOTATED_BY);
        assertRelation(my, d, DependencyType.ANNOTATED_BY);
        assertRelation(my, e, DependencyType.ANNOTATED_BY);
        assertRelation(my, f, DependencyType.ANNOTATED_BY);
    }


    /**
     * Test that relations to Exceptions are found
     * <p>
     * Exceptions in method signatures (declared exceptions)
     * Exceptions in catch blocks
     */
    @Test
    public void testExceptions() {
        dependencyGraph = initJavaScopeReader(MyClass.class, false);

        assertThat(dependencyGraph.getAllNodes().size(), is(4));

        Node myClass = getNode("de.qaware.qav.test.exceptions.MyClass");
        Node rt1 = getNode("de.qaware.qav.test.exceptions.MyRTE1"); // from throw new MyRTE1()
        Node rt2 = getNode("de.qaware.qav.test.exceptions.MyRTE2"); // from catch(MyRTE2 e)
        Node ioe = getNode("java.io.IOException"); // from f() throws IOException { }

        assertRelation(myClass, rt1, DependencyType.CREATE);
        assertRelation(myClass, rt2, DependencyType.READ_ONLY);
        assertRelation(myClass, ioe, DependencyType.REFERENCE);
    }

    @Test
    public void testFieldAccess() {
        dependencyGraph = initJavaScopeReader(MyField.class, false);

        assertThat(dependencyGraph.getAllNodes().size(), is(9));

        Node myField = getNode("de.qaware.qav.test.fields.MyField");
        Node a = getNode("de.qaware.qav.test.fields.A");
        Node b = getNode("de.qaware.qav.test.fields.B");
        Node c = getNode("de.qaware.qav.test.fields.C");
        Node d = getNode("de.qaware.qav.test.fields.D");
        Node e = getNode("de.qaware.qav.test.fields.E");
        Node f = getNode("de.qaware.qav.test.fields.F");

        assertRelation(myField, a, DependencyType.REFERENCE);
        assertRelation(myField, b, DependencyType.READ_WRITE);
        assertRelation(myField, c, DependencyType.READ_WRITE);
        assertRelation(myField, d, DependencyType.REFERENCE);
        assertRelation(myField, e, DependencyType.READ_ONLY);
        assertRelation(myField, f, DependencyType.REFERENCE);
    }

    @Test
    public void testGenericsFields() {
        dependencyGraph = initJavaScopeReader(MyGenericsFields.class, false);
        assertThat(dependencyGraph.getAllNodes().size(), is(11));

        Node my = getNode("de.qaware.qav.test.generics.MyGenericsFields");
        Node a = getNode("de.qaware.qav.test.generics.A");
        Node b = getNode("de.qaware.qav.test.generics.B");
        Node c = getNode("de.qaware.qav.test.generics.C");
        Node d = getNode("de.qaware.qav.test.generics.D");

        assertRelation(my, a, DependencyType.REFERENCE);
        assertRelation(my, b, DependencyType.REFERENCE);
        assertRelation(my, c, DependencyType.REFERENCE);
        assertRelation(my, d, DependencyType.REFERENCE);

        Node myList = getNode("de.qaware.qav.test.generics.MyList");
        Node list = getNode("java.util.List");
        assertRelation(myList, list, DependencyType.INHERIT);
        assertRelation(myList, a, DependencyType.REFERENCE);
    }

    @Test
    public void testGenericsMethods() {
        dependencyGraph = initJavaScopeReader(MyGenericsMethods.class, false);
        assertThat(dependencyGraph.getAllNodes().size(), is(11));

        Node my = getNode("de.qaware.qav.test.generics.MyGenericsMethods");
        Node a = getNode("de.qaware.qav.test.generics.A");
        Node b = getNode("de.qaware.qav.test.generics.B");
        Node c = getNode("de.qaware.qav.test.generics.C");
        Node d = getNode("de.qaware.qav.test.generics.D");

        assertRelation(my, a, DependencyType.REFERENCE);
        assertRelation(my, b, DependencyType.REFERENCE);
        assertRelation(my, c, DependencyType.REFERENCE);
        assertNoRelation(my, d);
    }

    @Test
    public void testInheritance() {
        dependencyGraph = initJavaScopeReader(MyInheritance.class, true);
        assertThat(dependencyGraph.getAllNodes().size(), is(8));
        assertThat(dependencyGraph.getAllEdges().size(), is(6));

        Node a = getNode("de.qaware.qav.test.inheritance.A");
        Node b = getNode("de.qaware.qav.test.inheritance.B");
        Node c = getNode("de.qaware.qav.test.inheritance.C");
        Node d = getNode("de.qaware.qav.test.inheritance.D");
        Node e = getNode("de.qaware.qav.test.inheritance.E");
        Node f = getNode("de.qaware.qav.test.inheritance.F");
        Node g = getNode("de.qaware.qav.test.inheritance.G");

        assertRelation(b, a, DependencyType.INHERIT);
        assertRelation(c, b, DependencyType.INHERIT);
        assertNoRelation(c, a); // not direct
        assertRelation(f, d, DependencyType.INHERIT);
        assertRelation(g, a, DependencyType.INHERIT);
        assertRelation(g, d, DependencyType.INHERIT);
        assertRelation(g, e, DependencyType.INHERIT);
    }

    @Test
    public void testInnerClasses() {
        dependencyGraph = initJavaScopeReader(MyInner.class, false);
        assertThat(dependencyGraph.getAllNodes().size(), is(3));
        assertThat(dependencyGraph.getAllEdges().size(), is(3));

        Node my = getNode("de.qaware.qav.test.inner.MyInner");
        Node myNested = getNode("de.qaware.qav.test.inner.MyInner$Inner");
        Node a = getNode("de.qaware.qav.test.inner.A");

        assertRelation(myNested, a, DependencyType.CREATE);
        assertRelation(my, myNested, DependencyType.CREATE);
    }

    @Test
    public void testCollapseInnerClasses() {
        dependencyGraph = initJavaScopeReader(MyInner.class, true);
        assertThat(dependencyGraph.getAllNodes().size(), is(2));
        assertThat(dependencyGraph.getAllEdges().size(), is(1));

        Node my = getNode("de.qaware.qav.test.inner.MyInner");
        assertNoNode("de.qaware.qav.test.inner.MyInner$Inner");
        Node a = getNode("de.qaware.qav.test.inner.A");

        assertRelation(my, a, DependencyType.CREATE);
    }

    @Test
    public void testInstructions() {
        dependencyGraph = initJavaScopeReader(MyInstructions.class, false);

        assertThat(dependencyGraph.getAllNodes().size(), is(5));

        Node my = getNode("de.qaware.qav.test.instructions.MyInstructions");
        Node a = getNode("de.qaware.qav.test.instructions.A");
        Node b = getNode("de.qaware.qav.test.instructions.B");
        Node c = getNode("de.qaware.qav.test.instructions.C");
        Node d = getNode("de.qaware.qav.test.instructions.D");

        assertRelation(my, a, DependencyType.REFERENCE);
        assertRelation(my, b, DependencyType.REFERENCE);
        assertRelation(my, c, DependencyType.CREATE);
        assertRelation(my, d, DependencyType.CREATE);
        assertRelation(b, a, DependencyType.INHERIT);
    }

    @Test
    public void testMethodSignatures() {
        dependencyGraph = initJavaScopeReader(MyMethods.class, false);

        assertThat(dependencyGraph.getAllNodes().size(), is(14));

        Node myMethods = getNode("de.qaware.qav.test.methods.MyMethods");
        Node a = getNode("de.qaware.qav.test.methods.A");
        Node b = getNode("de.qaware.qav.test.methods.B");
        Node c = getNode("de.qaware.qav.test.methods.C");
        Node d = getNode("de.qaware.qav.test.methods.D");
        Node e = getNode("de.qaware.qav.test.methods.E");
        Node f = getNode("de.qaware.qav.test.methods.IF");
        Node g = getNode("de.qaware.qav.test.methods.G");
        Node h = getNode("de.qaware.qav.test.methods.IH");
        getNode("java.util.List");

        assertRelation(myMethods, a, DependencyType.REFERENCE);
        assertRelation(myMethods, b, DependencyType.REFERENCE);
        assertRelation(myMethods, c, DependencyType.REFERENCE);
        assertRelation(myMethods, d, DependencyType.REFERENCE);
        assertRelation(myMethods, e, DependencyType.READ_ONLY);
        assertRelation(myMethods, f, DependencyType.READ_WRITE);
        assertRelation(myMethods, g, DependencyType.CREATE);
        assertRelation(myMethods, h, DependencyType.REFERENCE);
    }

    @Test
    public void testPrimitives() {
        dependencyGraph = initJavaScopeReader(MyPrimitives.class, false);

        assertThat(dependencyGraph.getAllNodes(), hasSize(2));
        Node myPrimitives = getNode("de.qaware.qav.test.primitives.MyPrimitives");
        Node myBoxed= getNode("de.qaware.qav.test.primitives.MyBoxed");

        assertThat(myPrimitives, notNullValue());
        assertThat(myBoxed, notNullValue());
    }

    @Test
    public void testReferences() {
        dependencyGraph = initJavaScopeReader(MyReference.class, false);

        // classes: A, B, MyReference, java.io.PrintStream
        assertThat(dependencyGraph.getAllNodes().size(), is(5));

        Node myReference = getNode("de.qaware.qav.test.reference.MyReference");
        Node a = getNode("de.qaware.qav.test.reference.A");
        Node b = getNode("de.qaware.qav.test.reference.B");
        Node c = getNode("de.qaware.qav.test.reference.C");
        Node printer = getNode("java.io.PrintStream");

        // the compiler optimizes the access to the static final String. There is no relation to A any more.
        // decompile the byte code to confirm this.
        assertNoRelation(myReference, a);
        assertRelation(myReference, b, DependencyType.READ_ONLY);
        assertRelation(myReference, printer, DependencyType.READ_WRITE);
        assertRelation(c, a, DependencyType.REFERENCE);
    }

    @Test
    public void testWrongInputDir() {
        dependencyGraph = DependencyGraphFactory.createGraph();
        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("baseDir", "not-existing");
        new JavaScopeReader(dependencyGraph, true).read(parameters);

        assertThat(dependencyGraph.getAllNodes(), hasSize(0));
    }

    @Test
    public void testNoInputDir() {
        dependencyGraph = DependencyGraphFactory.createGraph();
        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("baseDir", "src/test/resources/something.txt");
        new JavaScopeReader(dependencyGraph, true).read(parameters);

        assertThat(dependencyGraph.getAllNodes(), hasSize(0));
    }

    private DependencyGraph initJavaScopeReader(Class<?> startClass, boolean collapseInnerClasses) {
        DependencyGraph dependencyGraph = DependencyGraphFactory.createGraph();

        String packageName = startClass.getPackage().getName();
        String rootDir = TEST_CLASSES_ROOT + "/" + packageName.replaceAll("\\.", "/");

        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("baseDir", rootDir);
        new JavaScopeReader(dependencyGraph, collapseInnerClasses).read(parameters);

        writeGraph(dependencyGraph, startClass);

        return dependencyGraph;
    }

    private Node getNode(String name) {
        Node result = dependencyGraph.getNode(name);
        assertThat("Node " + name + " not in the graph", result, notNullValue());
        return result;
    }

    private void assertNoNode(String name) {
        Node result = dependencyGraph.getNode(name);
        assertThat("Node " + name + " should not be in the graph", result, nullValue());
    }

    private void assertRelation(Node from, Node to, DependencyType type) {
        Dependency edge = dependencyGraph.getEdge(from, to);
        assertThat(from.getName() + " -> " + to.getName() + " is missing", edge, notNullValue());
        assertThat(from.getName() + " -> " + to.getName() + " wrong type", edge.getDependencyType(), is(type));
    }

    private void assertNoRelation(Node from, Node to) {
        Dependency edge = dependencyGraph.getEdge(from, to);
        assertThat(from.getName() + " -> " + to.getName() + " should not be here", edge, nullValue());
    }

    private void writeGraph(DependencyGraph dependencyGraph, Class<?> baseClass) {
        new File(BUILD_TEST_OUTPUT_DIR).mkdirs();
        ArrayList<Abbreviation> abbreviationsList = Lists.newArrayList();
        String packageName = baseClass.getPackage().getName();
        String[] packageNames = packageName.split("\\.");
        String shortPackageName = packageNames[packageNames.length - 1];
        abbreviationsList.add(new Abbreviation(packageName, "T"));

        Architecture packageArchitecture = new DefaultPackageArchitectureFactory(dependencyGraph).createArchitecture();
        GraphExporter.export(dependencyGraph, BUILD_TEST_OUTPUT_DIR + "/" + shortPackageName, packageArchitecture, abbreviationsList, true);
        // GraphReaderWriter.write(dependencyGraph, BUILD_TEST_OUTPUT_DIR + "/" + shortPackageName + ".json");
    }
}
