package wang.liangchen.matrix.framework.commons.logging;

import wang.liangchen.matrix.framework.commons.StringUtil;

/**
 * @author Liangchen.Wang 2022-06-20 11:49
 */
class Slf4jLoggerImpl implements Logger {
    private final org.slf4j.Logger nativeLogger;

    public Slf4jLoggerImpl(org.slf4j.Logger nativeLogger) {
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
