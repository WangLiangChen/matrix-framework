package wang.liangchen.matrix.framework.commons.logging.slf4j;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.slf4j.spi.LocationAwareLogger;
import wang.liangchen.matrix.framework.commons.logging.MatrixLogger;
import wang.liangchen.matrix.framework.commons.logging.MatrixLoggerFactory;

/**
 * @author Liangchen.Wang 2022-06-20 11:54
 */
public class Slf4JLocationAwareMatrixLoggerImpl implements MatrixLogger {
    private static final Marker MARKER = MarkerFactory.getMarker(MatrixLoggerFactory.MARKER);

    private static final String FQCN = MatrixSlf4jImpl.class.getName();

    private final LocationAwareLogger nativeLogger;

    public Slf4JLocationAwareMatrixLoggerImpl(LocationAwareLogger nativeLogger) {
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
        nativeLogger.log(MARKER, FQCN, LocationAwareLogger.ERROR_INT, message, null, e);
    }

    @Override
    public void error(String message) {
        nativeLogger.log(MARKER, FQCN, LocationAwareLogger.ERROR_INT, message, null, null);
    }

    @Override
    public void debug(String message) {
        nativeLogger.log(MARKER, FQCN, LocationAwareLogger.DEBUG_INT, message, null, null);
    }

    @Override
    public void trace(String message) {
        nativeLogger.log(MARKER, FQCN, LocationAwareLogger.TRACE_INT, message, null, null);
    }

    @Override
    public void warn(String message) {
        nativeLogger.log(MARKER, FQCN, LocationAwareLogger.WARN_INT, message, null, null);
    }
}
