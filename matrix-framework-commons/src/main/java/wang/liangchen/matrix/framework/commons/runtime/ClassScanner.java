package wang.liangchen.matrix.framework.commons.runtime;

import wang.liangchen.matrix.framework.commons.enumeration.Symbol;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.commons.string.StringUtil;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * @author Liangchen.Wang 2022-05-30 22:50
 */
public enum ClassScanner {
    INSTANCE;
    private static final String JAVA_CLASS_PATH = "java.class.path";
    private static final String JAR = ".jar";
    private static final String CLASS = ".class";
    private static final String JAR_PROTOCOL = "jar";
    private static final String FILE_PROTOCOL = "file";
    private static final Set<Class<?>> classes = new HashSet<>();

    static {
        try {
            scanClassPath();
        } catch (Exception e) {
            throw new MatrixErrorException(e);
        }
    }

    private static void scanClassPath() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = classLoader.getResources("");
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            String protocol = url.getProtocol();
            switch (protocol) {
                case FILE_PROTOCOL:
                    Path path = Paths.get(url.toURI());
                    classes.addAll(classesFromDirectory(path));
                    break;
                case JAR_PROTOCOL:
                    classes.addAll(classesFromJar(url));
                    break;
            }
        }
    }

    private static void scanAllClassPath() throws Exception {
        String[] javaClassPaths = System.getProperty(JAVA_CLASS_PATH).split(Symbol.SEMICOLON.getSymbol());
        for (String javaClassPath : javaClassPaths) {
            Path path = Paths.get(javaClassPath);
        }
    }

    private static Set<Class<?>> classesFromJar(URL url) throws IOException {
        Set<Class<?>> innerClasses = new HashSet<>();
        JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
        JarFile jarFile = jarURLConnection.getJarFile();
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            String jarEntryName = jarEntry.getName();
            if (jarEntryName.endsWith(CLASS)) {
                String className = classPath2ClassName(jarEntryName);
                innerClasses.add(classForName(className));
            }
        }
        return innerClasses;
    }

    private static Set<String> classesFromJar(Path path) throws IOException {
        Set<String> innerClasses = new HashSet<>();
        try (JarInputStream jarInputStream = new JarInputStream(Files.newInputStream(path))) {
            JarEntry jarEntry = jarInputStream.getNextJarEntry();
            while (null != jarEntry) {
                String jarEntryName = jarEntry.getName();
                if (jarEntryName.endsWith(CLASS)) {
                    String className = classPath2ClassName(jarEntryName);
                    innerClasses.add(className);
                }
                jarEntry = jarInputStream.getNextJarEntry();
            }
            jarInputStream.closeEntry();
        }
        return innerClasses;
    }

    private static Set<Class<?>> classesFromDirectory(Path path) throws IOException {
        try (Stream<Path> stream = Files.walk(path)) {
            return stream.filter(subPath -> subPath.toString().endsWith(CLASS))
                    .map(path::relativize)
                    .map(subPath -> classPath2ClassName(subPath.toString()))
                    .map(ClassScanner::classForName)
                    .collect(Collectors.toSet());
        }
    }

    private static String classPath2ClassName(String classPath) {
        String className = StringUtil.INSTANCE.path2Package(classPath);
        className = className.substring(0, className.lastIndexOf(CLASS));
        return className;
    }

    private static Class<?> classForName(String className) {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            return classLoader.loadClass(className);
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            // ignore
            return ClassScanner.class;
        }
    }

    public Set<Class<?>> getClasses() {
        return classes;
    }

    public Set<Class<?>> getClasses(Predicate<Class<?>> predicate) {
        return classes.stream().filter(predicate).collect(Collectors.toSet());
    }

    public <A extends Annotation> Set<Class<?>> getAnnotatedClasses(Class<A> annotationClass) {
        return getClasses(clazz -> null != clazz.getAnnotation(annotationClass));
    }

    public Set<Class<?>> getImplementedClasses(Class<?> parentClass) {
        return getClasses(parentClass::isAssignableFrom);
    }

}