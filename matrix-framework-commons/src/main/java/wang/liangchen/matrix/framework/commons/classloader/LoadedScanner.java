package wang.liangchen.matrix.framework.commons.classloader;

import wang.liangchen.matrix.framework.commons.enumeration.Symbol;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Liangchen.Wang 2022-05-30 22:50
 */
public enum LoadedScanner {
    INSTANCE;
    private static final String JAVA_CLASS_PATH = "java.class.path";
    private static final String JAR = ".jar";
    private static final String CLASS = ".class";
    private static final Set<String> jars = new HashSet<>();
    private static final Set<String> classes = new HashSet<>();

    static {
        scanClassPath();
    }

    private static void scanClassPath() {
        String[] javaClassPaths = System.getProperty(JAVA_CLASS_PATH).split(Symbol.SEMICOLON.getSymbol());
        for (String javaClassPath : javaClassPaths) {
            if (populate(javaClassPath)) {
                continue;
            }
            Path path = Paths.get(javaClassPath);
            if (Files.isRegularFile(path)) {
                continue;
            }
            try {
                Files.walk(path).forEach(p -> populate(p.toString()));
            } catch (IOException e) {
                throw new MatrixErrorException(e);
            }
        }
    }


    private static boolean populate(String path) {
        if (path.endsWith(JAR)) {
            jars.add(path);
            return true;
        }
        if (path.endsWith(CLASS)) {
            classes.add(path);
            return true;
        }
        return false;
    }

    public Set<String> getJars() {
        return jars;
    }

    public Set<String> getClasses() {
        return classes;
    }
}
