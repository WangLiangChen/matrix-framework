package wang.liangchen.matrix.framework.commons.logging;

import org.slf4j.Logger;
import wang.liangchen.matrix.framework.commons.string.StringUtil;

/**
 * @author Liangchen.Wang 2022-06-20 11:49
 */
class MatrixSlf4jLoggerImpl implements MatrixLogger {
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
    public void error(Throwable e,String message,Object ... args) {
        nativeLogger.error(StringUtil.INSTANCE.format(message,args), e);
    }

    @Override
    public void error(String message,Object ... args) {
        nativeLogger.error(StringUtil.INSTANCE.format(message,args));
    }

    @Override
    public void debug(String message,Object ... args) {
        nativeLogger.debug(StringUtil.INSTANCE.format(message,args));
    }

    @Override
    public void trace(String message,Object ... args) {
        nativeLogger.trace(StringUtil.INSTANCE.format(message,args));
    }

    @Override
    public void warn(String message,Object ... args) {
        nativeLogger.warn(StringUtil.INSTANCE.format(message,args));
    }
}
