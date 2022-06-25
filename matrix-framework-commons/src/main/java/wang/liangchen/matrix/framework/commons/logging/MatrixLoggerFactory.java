package wang.liangchen.matrix.framework.commons.logging;

import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.commons.exception.MatrixInfoException;
import wang.liangchen.matrix.framework.commons.logging.jcl.MatrixJclImpl;
import wang.liangchen.matrix.framework.commons.logging.jul.MatrixJulImpl;
import wang.liangchen.matrix.framework.commons.logging.log4j2.MatrixLog4j2Impl;
import wang.liangchen.matrix.framework.commons.logging.slf4j.MatrixSlf4jImpl;
import wang.liangchen.matrix.framework.commons.logging.stdout.MatrixNoLoggingImpl;
import wang.liangchen.matrix.framework.commons.logging.stdout.MatrixStdOutImpl;

import java.lang.reflect.Constructor;

/**
 * @author Liangchen.Wang 2022-06-20 11:20
 */
public class MatrixLoggerFactory {
    public static final String MARKER = "MATRIX-FRAMEWORK";
    private static Constructor<? extends AbstractMatrixLogger> abstractMatrixLoggerConstructor;

    private MatrixLoggerFactory() {
    }

    static {
        tryImplementation(MatrixLoggerFactory::useSlf4jLogging);
        tryImplementation(MatrixLoggerFactory::useJcl);
        tryImplementation(MatrixLoggerFactory::useLog4J2Logging);
        tryImplementation(MatrixLoggerFactory::useJul);
        tryImplementation(MatrixLoggerFactory::useNoLogging);
    }


    public static MatrixLogger getLogger(String className) {
        try {
            // create a logger
            return abstractMatrixLoggerConstructor.newInstance(className);
        } catch (Throwable t) {
            throw new MatrixInfoException(t, "Error creating logger for {}.  Cause: {}", className, t.getMessage());
        }
    }

    public static MatrixLogger getLogger(Class<?> loggerClass) {
        return getLogger(loggerClass.getName());
    }

    public static synchronized void useLogging(Class<? extends AbstractMatrixLogger> abstractMatrixLoggerClass) {
        setImplementation(abstractMatrixLoggerClass);
    }

    public static synchronized void useSlf4jLogging() {
        setImplementation(MatrixSlf4jImpl.class);
    }

    public static synchronized void useJcl() {
        setImplementation(MatrixJclImpl.class);
    }

    public static synchronized void useLog4J2Logging() {
        setImplementation(MatrixLog4j2Impl.class);
    }

    public static synchronized void useJul() {
        setImplementation(MatrixJulImpl.class);
    }

    public static synchronized void useStdOutLogging() {
        setImplementation(MatrixStdOutImpl.class);
    }

    public static synchronized void useNoLogging() {
        setImplementation(MatrixNoLoggingImpl.class);
    }

    private static void tryImplementation(Runnable runnable) {
        if (null != abstractMatrixLoggerConstructor) {
            return;
        }
        try {
            runnable.run();
        } catch (Throwable t) {
            throw new MatrixErrorException(t);
        }
    }

    private static void setImplementation(Class<? extends AbstractMatrixLogger> abstractMatrixLoggerClass) {
        try {
            abstractMatrixLoggerConstructor = abstractMatrixLoggerClass.getConstructor(String.class);

            AbstractMatrixLogger logger = abstractMatrixLoggerConstructor.newInstance(MatrixLoggerFactory.class.getName());
            if (logger.isDebugEnabled()) {
                logger.debug("Logging initialized using '" + abstractMatrixLoggerClass + "' adapter.");
            }

        } catch (Throwable t) {
            throw new MatrixInfoException("Error setting Log implementation.  Cause: " + t, t);
        }
    }
}
