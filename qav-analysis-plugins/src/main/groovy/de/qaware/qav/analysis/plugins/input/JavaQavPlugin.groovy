package de.qaware.qav.analysis.plugins.input

import de.qaware.qav.analysis.plugins.base.BasePlugin
import de.qaware.qav.input.javacode.JavaScopeReader
import de.qaware.qav.doc.QavCommand
import de.qaware.qav.doc.QavPluginDoc
import de.qaware.qav.analysis.dsl.model.Analysis

/**
 * Provides the Java input for QAvalidator.
 *
 * @author QAware GmbH
 */
@QavPluginDoc(name = "JavaQavPlugin",
        description = "Provides the Java input for QAvalidator")
class JavaQavPlugin extends BasePlugin {

    @Override
    void apply(Analysis analysis) {
        super.apply(analysis)
        analysis.register("inputJava", this.&inputJava)
    }

    /**
     * Read all "*.class" files under the given directories, and add them to the
     * {@link de.qaware.qav.graph.api.DependencyGraph} which MUST exist in the context.
     * Collapses inner classes.
     *
     * @param classesRootDirNames the directories to check for *.class files recursively.
     */
    @QavCommand(name = "inputJava",
            description = """
                        Read all `"*.class"` files under the given directories, and add them to the
                        {@link DependencyGraph} which _must_ exist in the context.
                        Collapses inner classes, i.e. folds inner classes onto the outer class; all class-level 
                        relationships will be handled as if they would affect the outer class.
                        """,
            parameters = @QavCommand.Param(name = "classesRootDirNames",
                    description = """
                        Accepts one or more directories where it recursively searches all `*.class` files.
                        
                        This command may be called multiple times. The directory names are added (not replaced).

                        The `JavaQavPlugin` reads all the class files and analyzes them (using ASM). For each class, it
                        creates a node in the `context.dependencyGraph`, and for each dependency it creates an edge in
                        that graph. A dependency can be a method call to another class, or a class implementing an
                        interface, or a class calling the constructor of another class. The `JavaQavPlugin` uses
                        different dependency types for these relationships.
                        """)
    )
    void inputJava(String... classesRootDirNames) {
        inputJava(true, classesRootDirNames)
    }

    /**
     * Read all "*.class" files under the given directories, and add them to the
     * {@link de.qaware.qav.graph.api.DependencyGraph} which MUST exist in the context.
     * Lets the user decide if he wants to collapse inner classes.
     *
     * @param collapseInnerClasses Let the user decide if he want to collapse inner classes
     * @param classesRootDirNames the directories to check for *.class files recursively.
     */
    @QavCommand(name = "inputJava",
            description = """
                        Read all `"*.class"` files under the given directories, and add them to the
                        {@link DependencyGraph} which _must_ exist in the context.
                        Lets the user decide if he wants to collapse inner classes.
                        """,
            parameters = [@QavCommand.Param(name = "collapseInnerClasses",
                    description = """
                            `true` to collapse inner classes (the default behavior). Collapsing means that all 
                            class-level relationships will be handled as if they would affect the outer class.
                            `false` will preserve the full class names, like e.g. `YourClass\$1` or `OuterClass\$InnerClass`.
                            """),
                    @QavCommand.Param(name = "classesRootDirNames",
                            description = """
                        Accepts one or more directories where it recursively searches all `*.class` files.
                         
                        This command may be called multiple times. The directory names are added (not replaced).
                        For each call, it is possible to tell whether inner classes should be collapsed, i.e. folded 
                        onto the outer class, or not.

                        The `JavaQavPlugin` reads all the class files and analyzes them (using ASM). For each class, it
                        creates a node in the `context.dependencyGraph`, and for each dependency it creates an edge in
                        that graph. A dependency can be a method call to another class, or a class implementing an
                        interface, or a class calling the constructor of another class. The `JavaQavPlugin` uses
                        different dependency types for these relationships.
                        """)
            ]
    )
    void inputJava(boolean collapseInnerClasses, String... classesRootDirNames) {
        for (String rootDir : classesRootDirNames) {
            def parameters = [:]
            parameters.baseDir = rootDir
            inputJava(parameters, collapseInnerClasses)
        }
    }

    /**
     * Read all "*.class" files under the given directories, and add them to the
     * {@link de.qaware.qav.graph.api.DependencyGraph} which MUST exist in the context.
     *
     * @param parameters identify the class files to check. Uses the Ant DirectoryScanner.
     * @param collapseInnerClasses Let the user decide if he want to collapse inner classes; defaults to <tt>true</tt>
     */
    @QavCommand(name = "inputJava",
            description = """
                        Read all `"*.class"` files under the given directories, and add them to the
                        {@link DependencyGraph} which _must_ exist in the context.
                        Lets the user decide if he wants to collapse inner classes.
                        """,
            parameters = [
                    @QavCommand.Param(name = "parameters",
                            description = """
                        Accepts a map with `includes` and `excludes` patterns which work in Ant-style; this defines
                        where it recursively searches all `*.class` files.                        
                         
                        This command may be called multiple times. The directory names are added (not replaced).
                        For each call, it is possible to tell whether inner classes should be collapsed, i.e. folded 
                        onto the outer class, or not.

                        The `JavaQavPlugin` reads all the class files and analyzes them (using ASM). For each class, it
                        creates a node in the `context.dependencyGraph`, and for each dependency it creates an edge in
                        that graph. A dependency can be a method call to another class, or a class implementing an
                        interface, or a class calling the constructor of another class. The `JavaQavPlugin` uses
                        different dependency types for these relationships.
                        """),
                    @QavCommand.Param(name = "collapseInnerClasses",
                            description = """
                            `true` to collapse inner classes (the default behavior). Collapsing means that all 
                            class-level relationships will be handled as if they would affect the outer class.
                            `false` will preserve the full class names, like e.g. `YourClass\$1` or `OuterClass\$InnerClass`.
                            
                            Defaults to true.
                            """)
            ]
    )
    void inputJava(Map parameters, boolean collapseInnerClasses = true) {
        // provide a default: if not specified, read all *.class files.
        if (!parameters.includes) {
            parameters.includes = ["**/*.class", "**/*.jar"]
        }
        new JavaScopeReader(context.dependencyGraph, collapseInnerClasses).read(parameters)
    }
}
