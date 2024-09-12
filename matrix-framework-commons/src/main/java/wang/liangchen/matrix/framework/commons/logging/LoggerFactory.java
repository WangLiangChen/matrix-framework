package wang.liangchen.matrix.framework.commons.logging;

import wang.liangchen.matrix.framework.commons.exception.MatrixWarnException;

import java.lang.reflect.Constructor;

/**
 * @author Liangchen.Wang 2022-06-20 11:20
 */
public class LoggerFactory {
    public static final String MARKER = "MATRIX-FRAMEWORK";
    private static Constructor<? extends AbstractLogger> abstractLoggerConstructor;

    private LoggerFactory() {
    }

    static {
        tryImplementation(LoggerFactory::useSlf4jLogging);
        tryImplementation(LoggerFactory::useLog4J2Logging);
        tryImplementation(LoggerFactory::useJcl);
        tryImplementation(LoggerFactory::useJul);
        tryImplementation(LoggerFactory::useStdOutLogging);
        tryImplementation(LoggerFactory::useNoLogging);
    }


    public static Logger getLogger(String className) {
        try {
            return abstractLoggerConstructor.newInstance(className);
        } catch (Throwable t) {
            throw new MatrixWarnException(t, "Error creating logger for {}.  Cause: {}", className, t.getMessage());
        }
    }
    public static Logger getLogger(Class<?> loggerClass) {
        return getLogger(loggerClass.getName());
    }

    public static synchronized void useSlf4jLogging() {
        setImplementation(Slf4jImpl.class);
    }
    public static synchronized void useLog4J2Logging() {
        setImplementation(Log4j2Impl.class);
    }
    public static synchronized void useJcl() {
        setImplementation(JclImpl.class);
    }
    public static synchronized void useJul() {
        setImplementation(JulImpl.class);
    }
    public static synchronized void useStdOutLogging() {
        setImplementation(StdOutImpl.class);
    }
    public static synchronized void useNoLogging() {
        setImplementation(NoLoggingImpl.class);
    }

    private static void tryImplementation(Runnable runnable) {
        if (null != abstractLoggerConstructor) {
            return;
        }
        // setImplementation
        runnable.run();
    }

    private static void setImplementation(Class<? extends AbstractLogger> abstractLoggerClass) {
        try {
            abstractLoggerConstructor = abstractLoggerClass.getConstructor(String.class);

            AbstractLogger logger = abstractLoggerConstructor.newInstance(LoggerFactory.class.getName());
            if (logger.isDebugEnabled()) {
                logger.debug("Logging initialized using '" + abstractLoggerClass + "' adapter.");
            }

        } catch (Throwable t) {
            throw new MatrixWarnException("Error setting Log implementation.  Cause: " + t, t);
        }
    }
}
