package wang.liangchen.matrix.framework.commons.logging;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import wang.liangchen.matrix.framework.commons.StringUtil;

/**
 * @author Liangchen.Wang 2022-06-20 14:28
 */
class Log4j2LoggerImpl implements Logger {
    private static final Marker MARKER = MarkerManager.getMarker(LoggerFactory.MARKER);

    private final org.apache.logging.log4j.Logger nativeLogger;

    public Log4j2LoggerImpl(org.apache.logging.log4j.Logger nativeLogger) {
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
    public void error(Throwable e, String message, Object... args) {
        nativeLogger.error(MARKER, StringUtil.INSTANCE.format(message, args), e);
    }

    @Override
    public void error(String message, Object... args) {
        nativeLogger.error(MARKER, StringUtil.INSTANCE.format(message, args));
    }

    @Override
    public void debug(String message, Object... args) {
        nativeLogger.debug(MARKER, StringUtil.INSTANCE.format(message, args));
    }

    @Override
    public void trace(String message, Object... args) {
        nativeLogger.trace(MARKER, StringUtil.INSTANCE.format(message, args));
    }

    @Override
    public void warn(String message, Object... args) {
        nativeLogger.warn(MARKER, StringUtil.INSTANCE.format(message, args));
    }
}
