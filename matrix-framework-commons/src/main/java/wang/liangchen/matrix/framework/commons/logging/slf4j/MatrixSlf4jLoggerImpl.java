package wang.liangchen.matrix.framework.commons.logging.slf4j;

import org.slf4j.Logger;
import wang.liangchen.matrix.framework.commons.logging.MatrixLogger;

/**
 * @author Liangchen.Wang 2022-06-20 11:49
 */
public class MatrixSlf4jLoggerImpl implements MatrixLogger {
    private final Logger nativeLogger;

    public MatrixSlf4jLoggerImpl(Logger nativeLogger) {
        this.nativeLogger = nativeLogger;
    }

    @Override
    public boolean isDebugEnabled() {
        return nativeLogger.isDebugEnabled();
    }

    @Override
    public boolean isTraceEnabled() {
        return nativeLogger.isTraceEnabled();
    }

    @Override
    public void error(String message, Throwable e) {
        nativeLogger.error(message, e);
    }

    @Override
    public void error(String message) {
        nativeLogger.error(message);
    }

    @Override
    public void debug(String message) {
        nativeLogger.debug(message);
    }

    @Override
    public void trace(String message) {
        nativeLogger.trace(message);
    }

    @Override
    public void warn(String message) {
        nativeLogger.warn(message);
    }
}
