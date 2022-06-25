package wang.liangchen.matrix.framework.commons.logging.slf4j;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.spi.LocationAwareLogger;
import wang.liangchen.matrix.framework.commons.logging.AbstractMatrixLogger;
import wang.liangchen.matrix.framework.commons.logging.MatrixLogger;

/**
 * @author Liangchen.Wang 2022-06-20 11:43
 */
public class MatrixSlf4jImpl extends AbstractMatrixLogger implements MatrixLogger {

    private MatrixLogger matrixLogger;

    public MatrixSlf4jImpl(String className) {
        super(className);
        Logger nativeLogger = LoggerFactory.getLogger(className);
        if (nativeLogger instanceof LocationAwareLogger) {
            try {
                // check for slf4j >= 1.6 method signature
                nativeLogger.getClass().getMethod("log", Marker.class, String.class, int.class, String.class, Object[].class, Throwable.class);
                matrixLogger = new Slf4JLocationAwareMatrixLoggerImpl((LocationAwareLogger) nativeLogger);
                return;
            } catch (SecurityException | NoSuchMethodException e) {
                // fail-back to Slf4jLoggerImpl
            }
        }
        // Logger is not LocationAwareLogger or slf4j version < 1.6
        matrixLogger = new MatrixSlf4jLoggerImpl(nativeLogger);
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
