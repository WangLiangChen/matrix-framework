package wang.liangchen.matrix.framework.commons.logging;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.spi.LocationAwareLogger;
import wang.liangchen.matrix.framework.commons.string.StringUtil;

/**
 * @author Liangchen.Wang 2022-06-20 11:43
 */
class MatrixSlf4jImpl extends AbstractMatrixLogger implements MatrixLogger {

    private MatrixLogger matrixLogger;

    public MatrixSlf4jImpl(String className) {
        super(className);
        Logger nativeLogger = LoggerFactory.getLogger(className);
        if (nativeLogger instanceof LocationAwareLogger) {
            try {
                // check for slf4j >= 1.6 method signature
                nativeLogger.getClass().getMethod("log", Marker.class, String.class, int.class, String.class, Object[].class, Throwable.class);
                matrixLogger = new MatrixSlf4JLocationAwareLoggerImpl((LocationAwareLogger) nativeLogger);
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
    public void error(Throwable e,String message, Object ... args) {
        matrixLogger.error(StringUtil.INSTANCE.format(message,args), e);
    }

    @Override
    public void error(String message,Object ... args) {
        matrixLogger.error(StringUtil.INSTANCE.format(message,args));
    }

    @Override
    public void debug(String message,Object ... args) {
        matrixLogger.debug(StringUtil.INSTANCE.format(message,args));
    }

    @Override
    public void trace(String message,Object ... args) {
        matrixLogger.trace(StringUtil.INSTANCE.format(message,args));
    }

    @Override
    public void warn(String message,Object ... args) {
        matrixLogger.warn(StringUtil.INSTANCE.format(message,args));
    }
}
