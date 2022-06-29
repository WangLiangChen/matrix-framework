package wang.liangchen.matrix.framework.commons.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.spi.AbstractLogger;
import wang.liangchen.matrix.framework.commons.string.StringUtil;

/**
 * @author Liangchen.Wang 2022-06-20 14:26
 */
class MatrixLog4j2Impl extends AbstractMatrixLogger implements MatrixLogger {
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
