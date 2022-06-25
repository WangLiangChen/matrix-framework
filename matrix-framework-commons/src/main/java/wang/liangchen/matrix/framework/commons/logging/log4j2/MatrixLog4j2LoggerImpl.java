package wang.liangchen.matrix.framework.commons.logging.log4j2;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import wang.liangchen.matrix.framework.commons.logging.MatrixLogger;
import wang.liangchen.matrix.framework.commons.logging.MatrixLoggerFactory;

/**
 * @author Liangchen.Wang 2022-06-20 14:28
 */
public class MatrixLog4j2LoggerImpl implements MatrixLogger {
    private static final Marker MARKER = MarkerManager.getMarker(MatrixLoggerFactory.MARKER);

    private final Logger nativeLogger;

    public MatrixLog4j2LoggerImpl(Logger nativeLogger) {
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
        nativeLogger.error(MARKER, message, e);
    }

    @Override
    public void error(String message) {
        nativeLogger.error(MARKER, message);
    }

    @Override
    public void debug(String message) {
        nativeLogger.debug(MARKER, message);
    }

    @Override
    public void trace(String message) {
        nativeLogger.trace(MARKER, message);
    }

    @Override
    public void warn(String message) {
        nativeLogger.warn(MARKER, message);
    }
}
