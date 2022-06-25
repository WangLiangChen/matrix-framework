package wang.liangchen.matrix.framework.commons.logging.log4j2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.spi.AbstractLogger;
import wang.liangchen.matrix.framework.commons.logging.AbstractMatrixLogger;
import wang.liangchen.matrix.framework.commons.logging.MatrixLogger;

/**
 * @author Liangchen.Wang 2022-06-20 14:26
 */
public class MatrixLog4j2Impl extends AbstractMatrixLogger implements MatrixLogger {
    private final MatrixLogger matrixLogger;

    public MatrixLog4j2Impl(String className) {
        super(className);
        Logger nativeLogger = LogManager.getLogger(className);
        if (nativeLogger instanceof AbstractLogger) {
            matrixLogger = new MatrixLog4j2AbstractLoggerImpl((AbstractLogger) nativeLogger);
        } else {
            matrixLogger = new MatrixLog4j2LoggerImpl(nativeLogger);
        }
    }


    @Override
    public boolean isDebugEnabled() {
        return matrixLogger.isDebugEnabled();
    }

    @Override
    public boolean isTraceEnabled() {
        return matrixLogger.isTraceEnabled();
    }

    @Override
    public void error(String message, Throwable e) {
        matrixLogger.error(message, e);
    }

    @Override
    public void error(String message) {
        matrixLogger.error(message);
    }

    @Override
    public void debug(String message) {
        matrixLogger.debug(message);
    }

    @Override
    public void trace(String message) {
        matrixLogger.trace(message);
    }

    @Override
    public void warn(String message) {
        matrixLogger.warn(message);
    }
}
