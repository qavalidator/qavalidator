package de.qaware.qav.input.javacode;

import de.qaware.qav.graph.api.Constants;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.Node;
import de.qaware.qav.util.FileNameUtil;
import de.qaware.qav.util.FileSystemUtil;
import de.qaware.qav.util.JarFileUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Reads *.class files and their dependencies, and puts them into a {@link DependencyGraph}.
 *
 * @author QAware GmbH
 */
public class JavaScopeReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(JavaScopeReader.class);

    private final DependencyReader dependencyReader;

    /**
     * Constructor.
     *
     * @param dependencyGraph      the graph where to write the classes and their dependencies
     * @param collapseInnerClasses if true, collapses the dependencies of an inner class onto the outer class
     */
    public JavaScopeReader(DependencyGraph dependencyGraph, boolean collapseInnerClasses) {
        this.dependencyReader = new DependencyReader(dependencyGraph, collapseInnerClasses);
    }

    /**
     * Read the files as defined by the parameters into the given graph.
     *
     * @param parameters the parameters, Ant-style with baseDir (mandatory), and includes (optional) and excludes (optional)
     */
    public void read(Map parameters) {
        String baseDirName = (String) parameters.get("baseDir");
        if (StringUtils.isEmpty(baseDirName)) {
            LOGGER.warn("baseDir missing - no files will be read!");
        } else {
            File baseDir = new File(baseDirName);
            if (!baseDir.exists()) {
                LOGGER.warn("baseDir {} missing - no files will be read!", baseDir.getAbsolutePath());
            } else if (baseDir.isDirectory()) {
                readDirectory(baseDir, parameters);
            } else {
                readJarFile(baseDir, parameters);
            }
        }
    }

    private void readDirectory(File baseDir, Map parameters) {
        List<File> classFiles = FileNameUtil.identifyFiles(parameters);
        LOGGER.info("inputDir {}: Files to read: {}", baseDir.getAbsolutePath(), classFiles.size());

        classFiles.forEach(f -> {
            LOGGER.debug(f.getAbsolutePath());
            if (JarFileUtil.isJarEntry(f.getName())) {
                readJarFile(f, parameters);
            } else {
                byte[] classAsBytes = FileSystemUtil.readBytesFromFile(f.getAbsolutePath());
                readClass(classAsBytes);
            }
        });
    }

    private void readJarFile(File baseDir, Map parameters) {
        JarFileUtil.readJarFile(baseDir, parameters, (name, content) -> readClass(content));
    }

    /**
     * read the class and put a tag on the node to mark it as "INPUT".
     *
     * @param classAsBytes     the class, as byte array
     */
    private void readClass(byte[] classAsBytes) {
        Node node = dependencyReader.readDependencies(classAsBytes);
        node.setProperty(Constants.SCOPE, "input");
    }

}
