package wang.liangchen.matrix.framework.commons.logging;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.slf4j.spi.LocationAwareLogger;
import wang.liangchen.matrix.framework.commons.StringUtil;

/**
 * @author Liangchen.Wang 2022-06-20 11:54
 */
class Slf4JLocationAwareLoggerImpl implements Logger {
    private static final Marker MARKER = MarkerFactory.getMarker(LoggerFactory.MARKER);

    private static final String FQCN = Slf4jImpl.class.getName();

    private final LocationAwareLogger nativeLogger;

    public Slf4JLocationAwareLoggerImpl(LocationAwareLogger nativeLogger) {
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
        nativeLogger.log(MARKER, FQCN, LocationAwareLogger.ERROR_INT, StringUtil.INSTANCE.format(message, args), null, e);
    }

    @Override
    public void error(String message, Object... args) {
        nativeLogger.log(MARKER, FQCN, LocationAwareLogger.ERROR_INT, StringUtil.INSTANCE.format(message, args), null, null);
    }

    @Override
    public void debug(String message, Object... args) {
        nativeLogger.log(MARKER, FQCN, LocationAwareLogger.DEBUG_INT, StringUtil.INSTANCE.format(message, args), null, null);
    }

    @Override
    public void trace(String message, Object... args) {
        nativeLogger.log(MARKER, FQCN, LocationAwareLogger.TRACE_INT, StringUtil.INSTANCE.format(message, args), null, null);
    }

    @Override
    public void warn(String message, Object... args) {
        nativeLogger.log(MARKER, FQCN, LocationAwareLogger.WARN_INT, StringUtil.INSTANCE.format(message, args), null, null);
    }
}
